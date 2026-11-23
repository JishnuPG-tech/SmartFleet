#!/usr/bin/env python3
"""Pixel-compare Compose screenshots with reference PNGs.
Usage: python tools/compare.py <compose_png_dir> <reference_png_dir> [--out report]
Compose captures must be 648x1296 px (324x648 dp @2x, NO system bars). Reference PNGs are ~648x1362 (324x680 css px @2x
including the 32px fake status bar), so the top 64 px is cropped and the result trimmed to 648x1296 before comparing.
File names must match the reference: <ID>_<slug>.png. Missing files FAIL. Fixed masks for mockup defects F1/F2 are read from reference/masks.json (applied to both images). Exit code 1 if anything fails.
Metric: after a 1px blur, % of pixels whose max channel diff > 32 (of 255). PASS <= 1.5 %. Do not edit thresholds to pass."""
import sys, glob, os, json
import numpy as np
from PIL import Image, ImageFilter
cap, ref = sys.argv[1], sys.argv[2]
out = sys.argv[sys.argv.index("--out")+1] if "--out" in sys.argv else "compare_report"
os.makedirs(out, exist_ok=True)
mp=os.path.join(os.path.dirname(os.path.abspath(ref.rstrip("/"))),"..","masks.json")
MASKS=json.load(open(mp))["masks"] if os.path.exists(mp) else {}
rows, fails = [], 0
for rf in sorted(glob.glob(os.path.join(ref,"*.png"))):
    name=os.path.basename(rf); cf=os.path.join(cap,name)
    if not os.path.exists(cf): rows.append((name,None,"MISSING")); fails+=1; continue
    r=Image.open(rf).convert("RGB"); r=r.crop((0,64,648,64+1296))
    c=Image.open(cf).convert("RGB")
    if c.size!=r.size: rows.append((name,None,f"SIZE {c.size} != {r.size}")); fails+=1; continue
    for (mx,my,mw,mh) in MASKS.get(name[:-4],[]):
        box=(int(mx*2),int(my*2),int((mx+mw)*2),int((my+mh)*2))
        for im in (r,c): im.paste((0,0,0),box)
    a=np.asarray(r.filter(ImageFilter.GaussianBlur(1)),dtype=int); b=np.asarray(c.filter(ImageFilter.GaussianBlur(1)),dtype=int)
    d=np.abs(a-b).max(axis=2); pct=float((d>32).mean()*100)
    ok=pct<=1.5; fails+=0 if ok else 1
    rows.append((name,round(pct,2),"PASS" if ok else "FAIL"))
    if not ok:
        diff=np.zeros((*d.shape,3),dtype=np.uint8); diff[...,0]=np.clip(d*4,0,255)
        Image.fromarray(np.concatenate([np.asarray(r),np.asarray(c),diff],axis=1)).save(os.path.join(out,"diff_"+name))
with open(os.path.join(out,"report.md"),"w") as f:
    f.write("| screen | % pixels off | result |\n|---|---|---|\n")
    for n,p,s in rows: f.write(f"| {n} | {p if p is not None else '-'} | {s} |\n")
    f.write(f"\n{len(rows)-fails}/{len(rows)} pass\n")
print(open(os.path.join(out,"report.md")).read()[-400:]); sys.exit(1 if fails else 0)
