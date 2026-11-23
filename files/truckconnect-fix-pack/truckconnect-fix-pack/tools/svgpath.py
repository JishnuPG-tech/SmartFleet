"""SVG path -> absolute M/L/C/Q/Z only (arcs converted to cubic Beziers, compact arc flags handled).
Android's pathData parser mis-reads compact flags such as 'a7 7 0 0114 0' on some versions, which breaks icons.
Using only M/L/C/Q/Z removes that risk completely."""
import math, re
NUM = re.compile(r'[-+]?(?:\d+\.?\d*|\.\d+)(?:[eE][-+]?\d+)?')
def _arc_to_cubics(x1,y1,rx,ry,phi,fa,fs,x2,y2):
    if rx==0 or ry==0 or (x1==x2 and y1==y2): return [("L",x2,y2)]
    rx,ry=abs(rx),abs(ry); ph=math.radians(phi); c,s=math.cos(ph),math.sin(ph)
    dx,dy=(x1-x2)/2,(y1-y2)/2
    x1p=c*dx+s*dy; y1p=-s*dx+c*dy
    lam=(x1p**2)/(rx**2)+(y1p**2)/(ry**2)
    if lam>1: rx*=math.sqrt(lam); ry*=math.sqrt(lam)
    num=rx**2*ry**2-rx**2*y1p**2-ry**2*x1p**2; den=rx**2*y1p**2+ry**2*x1p**2
    co=math.sqrt(max(0,num/den)) if den else 0
    if fa==fs: co=-co
    cxp=co*rx*y1p/ry; cyp=-co*ry*x1p/rx
    cx=c*cxp-s*cyp+(x1+x2)/2; cy=s*cxp+c*cyp+(y1+y2)/2
    ang=lambda ux,uy,vx,vy: math.atan2(ux*vy-uy*vx, ux*vx+uy*vy)
    th1=ang(1,0,(x1p-cxp)/rx,(y1p-cyp)/ry)
    dth=ang((x1p-cxp)/rx,(y1p-cyp)/ry,(-x1p-cxp)/rx,(-y1p-cyp)/ry)
    if not fs and dth>0: dth-=2*math.pi
    elif fs and dth<0: dth+=2*math.pi
    n=max(1,math.ceil(abs(dth)/(math.pi/2)-1e-9)); d=dth/n; k=4/3*math.tan(d/4); out=[]
    for i in range(n):
        a1=th1+i*d; a2=a1+d
        def pt(a,dx_=0,dy_=0):
            x=rx*math.cos(a)+dx_; y=ry*math.sin(a)+dy_
            return (c*x-s*y+cx, s*x+c*y+cy)
        p1=(rx*(math.cos(a1)-k*math.sin(a1)), ry*(math.sin(a1)+k*math.cos(a1)))
        p2=(rx*(math.cos(a2)+k*math.sin(a2)), ry*(math.sin(a2)-k*math.cos(a2)))
        e=(rx*math.cos(a2), ry*math.sin(a2))
        tr=lambda p:(c*p[0]-s*p[1]+cx, s*p[0]+c*p[1]+cy)
        out.append(("C",)+tr(p1)+tr(p2)+tr(e))
    return out
def normalize(d):
    i=0; n=len(d); cmd=None; x=y=sx=sy=0.0; lc=None; out=[]
    def skip():
        nonlocal i
        while i<n and d[i] in ' ,\t\n': i+=1
    def num():
        nonlocal i
        skip(); m=NUM.match(d,i); i=m.end(); return float(m.group())
    def flag():
        nonlocal i
        skip(); v=d[i]; i+=1; return int(v)
    while True:
        skip()
        if i>=n: break
        if d[i].isalpha(): cmd=d[i]; i+=1
        elif cmd is None: raise ValueError("bad path")
        else:
            if cmd=='M': cmd='L'
            elif cmd=='m': cmd='l'
        up=cmd.upper(); rel=cmd.islower()
        if up=='Z':
            out.append(("Z",)); x,y=sx,sy; lc=None; continue
        if up=='M':
            nx,ny=num(),num()
            if rel: nx+=x; ny+=y
            x,y=nx,ny; sx,sy=x,y; out.append(("M",x,y)); lc=None
        elif up=='L':
            nx,ny=num(),num()
            if rel: nx+=x; ny+=y
            x,y=nx,ny; out.append(("L",x,y)); lc=None
        elif up=='H':
            nx=num(); nx=nx+x if rel else nx; x=nx; out.append(("L",x,y)); lc=None
        elif up=='V':
            ny=num(); ny=ny+y if rel else ny; y=ny; out.append(("L",x,y)); lc=None
        elif up=='C':
            v=[num() for _ in range(6)]
            if rel: v=[v[0]+x,v[1]+y,v[2]+x,v[3]+y,v[4]+x,v[5]+y]
            out.append(("C",*v)); lc=(v[2],v[3],'C'); x,y=v[4],v[5]
        elif up=='S':
            v=[num() for _ in range(4)]
            if rel: v=[v[0]+x,v[1]+y,v[2]+x,v[3]+y]
            c1=(2*x-lc[0],2*y-lc[1]) if lc and lc[2]=='C' else (x,y)
            out.append(("C",c1[0],c1[1],*v)); lc=(v[0],v[1],'C'); x,y=v[2],v[3]
        elif up=='Q':
            v=[num() for _ in range(4)]
            if rel: v=[v[0]+x,v[1]+y,v[2]+x,v[3]+y]
            out.append(("Q",*v)); lc=(v[0],v[1],'Q'); x,y=v[2],v[3]
        elif up=='T':
            v=[num(),num()]
            if rel: v=[v[0]+x,v[1]+y]
            c1=(2*x-lc[0],2*y-lc[1]) if lc and lc[2]=='Q' else (x,y)
            out.append(("Q",c1[0],c1[1],*v)); lc=(c1[0],c1[1],'Q'); x,y=v
        elif up=='A':
            rx,ry,rot=num(),num(),num(); fa=flag(); fs=flag(); nx,ny=num(),num()
            if rel: nx+=x; ny+=y
            out+=_arc_to_cubics(x,y,rx,ry,rot,fa,fs,nx,ny); x,y=nx,ny; lc=None
        else: raise ValueError("cmd "+cmd)
    f=lambda v:("%g"%round(v,3))
    return "".join(c[0]+",".join(f(v) for v in c[1:]) if len(c)>1 else "Z" for c in out) if False else "".join(
        (c[0]+" "+" ".join(f(v) for v in c[1:])) if len(c)>1 else "Z" for c in out)
