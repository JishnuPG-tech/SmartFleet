#!/usr/bin/env python3
"""Convert the IC icon dictionary in mockup/index.html into Android VectorDrawables.
Usage: python tools/gen_icons.py  -> app/src/main/res/drawable/ic_<name>.xml (24x24, stroke 1.8, round caps/joins)
All path data is normalised to absolute M/L/C/Q/Z (arcs converted to cubic curves) via svgpath.py, because Android mis-parses compact arc flags.
Icons are stroke-only and tint-able: use Icon(painterResource(R.drawable.ic_x), tint=...)."""
import re, json, subprocess, math, sys
sys.path.insert(0, str(__import__("pathlib").Path(__file__).resolve().parent))
from svgpath import normalize
from pathlib import Path
ROOT = Path(__file__).resolve().parent.parent
html = (ROOT/"mockup/index.html").read_text(encoding="utf-8")
m = re.search(r"const IC=\{(.*?)\};\s*\nconst ic=", html, re.S)
body = m.group(1)
js = "const IC={"+body+"};console.log(JSON.stringify(IC));"
IC = json.loads(subprocess.check_output(["node","-e",js]))
OUT = ROOT/"app/src/main/res/drawable"; OUT.mkdir(parents=True, exist_ok=True)
f = lambda v: ("%g" % round(float(v),3))
def circle(cx,cy,r):
    cx,cy,r=float(cx),float(cy),float(r)
    return f"M{f(cx-r)},{f(cy)}a{f(r)},{f(r)} 0 1,0 {f(2*r)},0a{f(r)},{f(r)} 0 1,0 {f(-2*r)},0z"
def rect(x,y,w,h,rx=0):
    x,y,w,h,rx=map(float,(x,y,w,h,rx))
    if rx==0: return f"M{f(x)},{f(y)}h{f(w)}v{f(h)}h{f(-w)}z"
    return (f"M{f(x+rx)},{f(y)}h{f(w-2*rx)}a{f(rx)},{f(rx)} 0 0,1 {f(rx)},{f(rx)}v{f(h-2*rx)}a{f(rx)},{f(rx)} 0 0,1 {f(-rx)},{f(rx)}"
            f"h{f(-(w-2*rx))}a{f(rx)},{f(rx)} 0 0,1 {f(-rx)},{f(-rx)}v{f(-(h-2*rx))}a{f(rx)},{f(rx)} 0 0,1 {f(rx)},{f(-rx)}z")
def attrs(tag): return dict(re.findall(r'(\w[\w-]*)="([^"]*)"', tag))
def norm_path(d):
    # expand compact arc flags (e.g. 'a2 2 0 004 0' -> 'a2 2 0 0 0 4 0') so every Android parser version reads them
    out=[];i=0
    toks=re.findall(r'[MmLlHhVvCcSsQqTtAaZz]|-?\d*\.?\d+(?:e-?\d+)?',d)
    res=[];cmd=None;args=[]
    return d  # keep original; compact flags are valid SVG and handled by AndroidX PathParser (verify in the icon sheet test)
count=0
for name, svg in IC.items():
    paths=[]
    for tag in re.findall(r"<(?:path|circle|rect)[^>]*/?>", svg):
        a=attrs(tag)
        if tag.startswith("<path"): paths.append(normalize(a["d"]))
        elif tag.startswith("<circle"): paths.append(normalize(circle(a["cx"],a["cy"],a["r"])))
        else: paths.append(normalize(rect(a.get("x",0),a.get("y",0),a["width"],a["height"],a.get("rx",0))))
    pd=" ".join(paths)
    xml=f'''<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:pathData="{pd}"
        android:strokeColor="#FF000000" android:strokeWidth="1.8"
        android:strokeLineCap="round" android:strokeLineJoin="round" android:fillColor="#00000000"/>
</vector>
'''
    (OUT/f"ic_{name}.xml").write_text(xml,encoding="utf-8"); count+=1
print(f"wrote {count} icons to {OUT}")
