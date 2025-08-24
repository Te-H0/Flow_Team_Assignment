// 공통 유틸 함수
const Utils = {
    temporarilyBlockEnter(ms = 200) {
        this.enterBlocked = true;
        setTimeout(() => this.enterBlocked = false, ms);
    },
    updateCount(countEl, delta) {
        countEl.textContent = String(parseInt(countEl.textContent, 10) + delta);
    },
    makeChip(id, name) {
        const span = document.createElement('span');
        span.className = 'chip';
        span.dataset.id = id;

        const label = document.createElement('span');
        label.textContent = name;

        const btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'chip-del';
        btn.textContent = '×';

        span.append(label, btn);
        return span;
    },
    enterBlocked: false
};

// 고정 확장자 토글
function initFixedExtensionToggles() {
    let toggleLocked = false;
    document.querySelectorAll('.fixed-toggle').forEach(cb => {
        cb.addEventListener('change', async () => {
            if (toggleLocked) {
                cb.checked = !cb.checked;
                return;
            }
            toggleLocked = true;

            const id = cb.dataset.id;
            const next = cb.checked;

            try {
                const res = await fetch(`/extensions/manage/fixed/${id}?active=${next}`, {
                    method: 'PATCH'
                });
                if (!res.ok) {
                    const msg = await res.text();
                    throw new Error(msg);
                }
            } catch (e) {
                cb.checked = !next;
                alert(e.message + '\n새로고침 해주세요');
            } finally {
                setTimeout(() => toggleLocked = false, 300);
            }
        });
    });
}

// 커스텀 확장자 입력 길이 제한
function initCustomInputLengthGuard() {
    const input = document.getElementById('custom-input');
    const maxLen = parseInt(input.getAttribute('maxlength'), 10);

    input.addEventListener('keydown', (e) => {
        if (e.ctrlKey || e.metaKey || e.altKey) return;
        if (input.value.length >= maxLen && e.key.length === 1) {
            e.preventDefault();
            alert(`확장자는 최대 ${maxLen}자까지 입력 가능합니다.`);
        }
    });
}

// 커스텀 확장자 추가/삭제 관리
function initCustomExtensionManager() {
    const input = document.getElementById('custom-input');
    const addBtn = document.getElementById('add-btn');
    const chips = document.getElementById('custom-chips');
    const count = document.getElementById('custom-count');
    const configEl = document.getElementById('extension-config');
    const extensionPattern = configEl.dataset.extensionRegex;
    const pattern = new RegExp(extensionPattern);

    const maxCount = parseInt(
        document.querySelector('.row[data-max-custom-count]').dataset.maxCustomCount,
        10
    );

    let addLocked = false;

    async function addCustom() {
        if (addLocked) return;
        addLocked = true;

        const name = input.value;
        if (!name) {
            addLocked = false;
            return;
        }

        if (!pattern.test(name)) {
            alert("확장자 형식이 어긋납니다.\n(소문자/숫자만 사용 가능, . 접두사/접미사 사용 불가, 공백 어느 위치든 절대 금지)");
            Utils.temporarilyBlockEnter(500);
            addLocked = false;
            return;
        }

        const currentCount = parseInt(count.textContent, 10);
        if (currentCount >= maxCount) {
            alert(`커스텀 확장자는 최대 ${maxCount}개까지만 추가할 수 있습니다.`);
            Utils.temporarilyBlockEnter(500);
            addLocked = false;
            return;
        }

        try {
            const res = await fetch('/extensions/manage/custom', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'},
                body: new URLSearchParams({name})
            });

            if (!res.ok) {
                const msg = await res.text();
                throw new Error(msg);
            }

            const data = await res.json();
            chips.prepend(Utils.makeChip(data.id, data.name));
            Utils.updateCount(count, +1);
            input.value = '';
            input.focus();
        } catch (e) {
            alert(e.message);
        } finally {
            setTimeout(() => addLocked = false, 500);
        }
    }

    async function removeCustom(id, chipEl) {
        try {
            const res = await fetch(`/extensions/manage/custom/${id}`, {method: 'DELETE'});
            if (!res.ok) {
                const msg = await res.text();
                throw new Error(msg || "삭제 실패");
            }
            chipEl.remove();
            Utils.updateCount(count, -1);
        } catch (e) {
            alert(e.message);
            location.reload();
        }
    }

    addBtn.addEventListener('click', addCustom);
    input.addEventListener('keyup', (e) => {
        if (e.key === 'Enter') {
            if (Utils.enterBlocked) return;
            e.preventDefault();
            addCustom();
        }
    });
    chips.addEventListener('click', (e) => {
        const btn = e.target.closest('.chip-del');
        if (!btn) return;
        removeCustom(btn.closest('.chip').dataset.id, btn.closest('.chip'));
    });
}

// 초기화
function initExtensionPage() {
    initFixedExtensionToggles();
    initCustomInputLengthGuard();
    initCustomExtensionManager();
}

document.addEventListener('DOMContentLoaded', initExtensionPage);