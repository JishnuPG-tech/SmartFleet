import json, re
from pathlib import Path

pairs = json.load(open('reference/strings_pairs.json', encoding='utf-8'))
untranslated = json.load(open('reference/strings_untranslated_in_ml.json', encoding='utf-8'))

def clean_key(screen, s):
    prefix = screen.lower()
    k = re.sub(r'[^a-zA-Z0-9_]+', '_', s).strip('_').lower()
    return f'{prefix}_{k[:35]}'

def escape_xml(s):
    s = s.replace('&', '&amp;')
    s = s.replace('<', '&lt;')
    s = s.replace('>', '&gt;')
    s = s.replace("'", "\\'")
    s = s.replace('"', '\\"')
    s = s.replace('\n', '\\n')
    return s

en_entries = {}
ml_entries = {}

for p in pairs:
    screen = p['screen']
    en = p['en']
    ml = p['ml']
    key = clean_key(screen, en)
    en_entries[key] = (en, False)
    ml_entries[key] = (ml, False)

for screen, slist in untranslated.items():
    for en in slist:
        key = clean_key(screen, en)
        if key not in en_entries:
            en_entries[key] = (en, False)
            ml_entries[key] = (en, True)

res_dir = Path('android/core-design/src/main/res')
(res_dir / 'values').mkdir(parents=True, exist_ok=True)
(res_dir / 'values-ml').mkdir(parents=True, exist_ok=True)

with open(res_dir / 'values/strings.xml', 'w', encoding='utf-8') as f:
    f.write('<?xml version="1.0" encoding="utf-8"?>\n<resources>\n')
    f.write('    <string name="app_name">TruckConnect</string>\n')
    for k in sorted(en_entries.keys()):
        text, _ = en_entries[k]
        f.write(f'    <string name="{k}">{escape_xml(text)}</string>\n')
    f.write('</resources>\n')

with open(res_dir / 'values-ml/strings.xml', 'w', encoding='utf-8') as f:
    f.write('<?xml version="1.0" encoding="utf-8"?>\n<resources>\n')
    f.write('    <string name="app_name">ട്രക്ക് കണക്ട്</string>\n')
    for k in sorted(ml_entries.keys()):
        text, is_todo = ml_entries[k]
        todo_tag = ' <!-- TODO-ML -->' if is_todo else ''
        f.write(f'    <string name="{k}">{escape_xml(text)}</string>{todo_tag}\n')
    f.write('</resources>\n')

print('Wrote values/strings.xml and values-ml/strings.xml successfully')
