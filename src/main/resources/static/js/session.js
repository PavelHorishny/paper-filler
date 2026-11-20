// ---------- Сессия: продлеваем, пока человек работает; сообщаем, когда закончилась ----------
const SESSION_MINUTES = 30;              // должно совпадать с таймаутом сессии на сервере
const PING_EVERY_MS   = 5 * 60 *1000;   // пинг не чаще раза в 5 минут

let lastServerContact = Date.now();      // когда сервер последний раз ответил
let wasActive = false;                   // что-то вводили или кликали с прошлого пинга

document.addEventListener('input', () => { wasActive = true; });
document.addEventListener('click', () => { wasActive = true; });

async function sessionAlive() {
    try {
        const response = await fetch('/api/ping', { redirect: 'manual' });
        if (response.ok) {
            lastServerContact = Date.now();
            return true;
        }
    } catch (e) {
        return true;                     // нет сети — не знаем; проверим в следующий раз
    }
    return false;                        // сервер отправил на вход — сессии нет
}

function showSessionEnded() {
    window.location.href = '/login-error';
}

// 1) Человек работает — продлеваем сессию
setInterval(async () => {
    if (!wasActive) return;
    wasActive = false;
    if (!(await sessionAlive())) showSessionEnded();
}, PING_EVERY_MS);

// 2) Долго не было связи с сервером — сессия точно закончилась
setInterval(() => {
    const idleMinutes = (Date.now() - lastServerContact) / 60000;
    if (idleMinutes >= SESSION_MINUTES) showSessionEnded();
}, 30 * 1000);

// 3) Вернулись на вкладку (ноутбук проснулся, переключились из другой программы) — проверяем сразу
document.addEventListener('visibilitychange', async () => {
    if (document.visibilityState === 'visible' && !(await sessionAlive())) {
        showSessionEnded();
    }
});
