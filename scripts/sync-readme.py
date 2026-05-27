#!/usr/bin/env python3
"""
sync-readme.py
──────────────
CLAUDE.md의 <!-- README:NAME -->...<!-- /README:NAME --> 블록을
README.md의 동일한 마커 블록 사이에 복사합니다.

사용:
  python3 scripts/sync-readme.py          # 기본 (CLAUDE.md → README.md)
  python3 scripts/sync-readme.py --check  # 변경 필요 여부만 확인 (CI용, exit 1 if drift)
"""

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
CLAUDE = ROOT / "CLAUDE.md"
README = ROOT / "README.md"

BLOCK_RE = re.compile(
    r"<!--\s*README:(?P<name>[A-Za-z0-9_\-]+)\s*-->"
    r"(?P<body>.*?)"
    r"<!--\s*/README:(?P=name)\s*-->",
    re.DOTALL,
)


def fail(msg: str) -> "None":
    print(f"[sync-readme] ERROR: {msg}", file=sys.stderr)
    sys.exit(1)


def extract_blocks(src: str) -> "dict[str, str]":
    """소스에서 <!-- README:X -->...<!-- /README:X --> 내용을 {이름: 본문} 으로 추출."""
    return {m.group("name"): m.group("body") for m in BLOCK_RE.finditer(src)}


def substitute(target: str, blocks: "dict[str, str]") -> str:
    """target의 각 <!-- README:X --> ... <!-- /README:X --> 블록 본문을 blocks[X]로 치환."""
    def repl(match: "re.Match[str]") -> str:
        name = match.group("name")
        body = blocks.get(name)
        if body is None:
            # 소스에 없는 블록이면 그대로 둠 (경고만)
            print(f"[sync-readme] warn: '{name}' 블록이 CLAUDE.md에 없음 — 그대로 둠")
            return match.group(0)
        # 본문 앞뒤 줄바꿈 정리: 항상 마커 직후/직전에 빈 줄을 한 번씩 둠
        normalized = "\n" + body.strip("\n") + "\n"
        return f"<!-- README:{name} -->{normalized}<!-- /README:{name} -->"

    return BLOCK_RE.sub(repl, target)


def main() -> int:
    check_only = "--check" in sys.argv

    if not CLAUDE.exists():
        fail(f"{CLAUDE} not found")
    if not README.exists():
        fail(f"{README} not found")

    claude_src = CLAUDE.read_text(encoding="utf-8")
    readme_src = README.read_text(encoding="utf-8")

    blocks = extract_blocks(claude_src)
    if not blocks:
        fail("CLAUDE.md 안에서 <!-- README:이름 --> 블록을 하나도 찾지 못했습니다.")

    new_readme = substitute(readme_src, blocks)

    if new_readme == readme_src:
        print("[sync-readme] README.md 이미 최신 상태")
        return 0

    if check_only:
        print("[sync-readme] README.md drift 감지됨 (--check)")
        return 1

    README.write_text(new_readme, encoding="utf-8")
    synced = ", ".join(sorted(blocks.keys()))
    print(f"[sync-readme] README.md 동기화 완료 — 블록: {synced}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
