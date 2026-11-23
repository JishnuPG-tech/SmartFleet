#!/usr/bin/env python3
"""
Capture reference screenshots + exact layout specs from mockup/index.html.

Usage (from repo root):
  pip install playwright && playwright install chromium
  python tools/capture_mockup.py                    # default matrix
  python tools/capture_mockup.py --all              # every theme x mode x lang x screen (large)

Outputs (under reference/):
  png/<theme>-<mode>-<lang>/<ID>_<slug>.png   phone screen only, 2x scale
  spec/<ID>_<slug>.html                       exact HTML of the screen (inline styles included)
  spec/<ID>_<slug>.layout.json                every element: box (x,y,w,h in CSS px relative
                                              to the phone screen), font-size, weight, colour,
                                              background, radius, border, text
Fonts are loaded from ./fonts (Plus Jakarta Sans + Noto Sans Malayalam) so rendering is
identical to the intended design even without internet.
"""
import argparse, json, re, sys
from pathlib import Path
from playwright.sync_api import sync_playwright

ROOT = Path(__file__).resolve().parent.parent
HTML = ROOT / "mockup" / "index.html"
FONTS = ROOT / "fonts"
OUT = ROOT / "reference"
TABS = ["auth", "admin", "driver", "settings"]
THEMES = ["indigo", "emerald", "graphite", "sunset"]

FONT_CSS = f"""
@font-face{{font-family:'Plus Jakarta Sans';font-weight:200 800;src:url('file://{FONTS}/plus_jakarta_sans_variable.ttf')}}
@font-face{{font-family:'Noto Sans Malayalam';font-weight:100 900;src:url('file://{FONTS}/noto_sans_malayalam_variable.ttf')}}
"""

JS_SPEC = """
(ph) => {
  const base = ph.getBoundingClientRect();
  const out = [];
  const walk = (el, depth) => {
    const r = el.getBoundingClientRect();
    const cs = getComputedStyle(el);
    if (r.width === 0 && r.height === 0) return;
    const own = [...el.childNodes].filter(n => n.nodeType === 3).map(n => n.textContent.trim()).join(' ').trim();
    out.push({
      d: depth, tag: el.tagName.toLowerCase(), cls: (el.getAttribute('class')||'').toString(),
      x: +(r.left-base.left).toFixed(1), y: +(r.top-base.top).toFixed(1), w: +r.width.toFixed(1), h: +r.height.toFixed(1),
      fs: cs.fontSize, fw: cs.fontWeight, ls: cs.letterSpacing, lh: cs.lineHeight, color: cs.color,
      bg: cs.backgroundColor, bgi: cs.backgroundImage !== 'none' ? cs.backgroundImage : undefined,
      radius: cs.borderTopLeftRadius, border: cs.borderTopWidth !== '0px' ? cs.borderTopWidth+' '+cs.borderTopColor : undefined,
      pad: cs.padding, gap: cs.gap !== 'normal' ? cs.gap : undefined, opacity: cs.opacity !== '1' ? cs.opacity : undefined,
      text: own || undefined
    });
    for (const c of el.children) walk(c, depth+1);
  };
  walk(ph, 0);
  return out;
}
"""

def slug(s): return re.sub(r'[^a-z0-9]+','_',s.lower()).strip('_')

def set_state(page, theme, mode, lang):
    page.evaluate("""([t,m,l]) => {
      document.querySelector(`[data-th="${t}"]`).click();
      document.querySelector(`[data-m="${m}"]`).click();
      document.querySelector(`[data-l="${l}"]`).click();
    }""", [theme, mode, lang])
    page.wait_for_timeout(120)

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--all", action="store_true")
    a = ap.parse_args()
    if a.all:
        matrix = [(t,m,l) for t in THEMES for m in ("light","dark") for l in ("en","ml")]
    else:
        matrix = [("indigo","light","en"),("indigo","light","ml"),("indigo","dark","en"),("indigo","dark","ml")]
    with sync_playwright() as p:
        b = p.chromium.launch(args=["--allow-file-access-from-files"])
        page = b.new_page(viewport={"width": 1500, "height": 1000}, device_scale_factor=2)
        page.goto(HTML.as_uri())
        page.add_style_tag(content=FONT_CSS)
        page.wait_for_timeout(300)
        page.evaluate("document.fonts.ready")
        spec_done = set()
        for theme, mode, lang in matrix:
            outdir = OUT / "png" / f"{theme}-{mode}-{lang}"
            outdir.mkdir(parents=True, exist_ok=True)
            for tab in TABS:
                page.click(f'[data-t="{tab}"]')
                set_state(page, theme, mode, lang)
                page.wait_for_timeout(150)
                figs = page.query_selector_all("figure.fig")
                for fg in figs:
                    sid = fg.query_selector("figcaption b i").inner_text().strip()
                    name = fg.query_selector("figcaption b").inner_text().replace(sid, "", 1).strip()
                    ph = fg.query_selector(".ph")
                    fn = f"{sid}_{slug(name)}"
                    ph.screenshot(path=str(outdir / f"{fn}.png"))
                    key = (fn, lang)
                    if theme == "indigo" and mode == "light" and key not in spec_done:
                        spec_done.add(key)
                        sd = OUT / "spec" / lang
                        sd.mkdir(parents=True, exist_ok=True)
                        (sd / f"{fn}.html").write_text(ph.evaluate("e=>e.outerHTML"), encoding="utf-8")
                        (sd / f"{fn}.layout.json").write_text(json.dumps(ph.evaluate(JS_SPEC), ensure_ascii=False, indent=0), encoding="utf-8")
            print("captured", theme, mode, lang, file=sys.stderr)
        b.close()

if __name__ == "__main__":
    main()
