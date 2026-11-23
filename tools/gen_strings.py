#!/usr/bin/env python3
"""Build the EN<->ML string table by diffing the two captured layouts (reference/spec/en vs /ml).
Output: reference/strings_pairs.json  [{screen, en, ml}] — every visible string whose Malayalam differs,
plus reference/strings_untranslated_in_ml.json — visible driver-screen strings that stay English in the
mockup's Malayalam mode (these need real translations later; keep EN text + a TODO in values-ml)."""
import json, glob
from pathlib import Path
R = Path(__file__).resolve().parent.parent/"reference"
pairs, same = {}, {}
for fe in sorted(glob.glob(str(R/"spec/en/*.layout.json"))):
    fm = fe.replace("/en/","/ml/")
    en, ml = json.load(open(fe)), json.load(open(fm))
    sid = Path(fe).name.split(".")[0]
    if len(en)!=len(ml): print("structure differs", sid); continue
    for a,b in zip(en,ml):
        ta,tb=a.get("text"),b.get("text")
        if not ta: continue
        if ta!=tb: pairs[(sid,ta)]=tb
        elif any(c.isalpha() for c in ta): same.setdefault(sid,set()).add(ta)
json.dump([{"screen":s,"en":e,"ml":m} for (s,e),m in pairs.items()], open(R/"strings_pairs.json","w"), ensure_ascii=False, indent=1)
json.dump({k:sorted(v) for k,v in same.items()}, open(R/"strings_untranslated_in_ml.json","w"), ensure_ascii=False, indent=1)
print(len(pairs),"EN/ML pairs;", sum(len(v) for v in same.values()),"strings identical in both languages (screens: %d)"%len(same))
