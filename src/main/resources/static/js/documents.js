function updateCompany(toggle) {
    const box = document.querySelector('fieldset[data-company="' + toggle.value + '"]');
    box.hidden = !toggle.checked;
    box.disabled = !toggle.checked;   // выключенный блок не отправляется на сервер
}

document.querySelectorAll('.company-toggle').forEach(toggle => {
    updateCompany(toggle);   // при загрузке: браузер мог запомнить галочку после «Назад»
    toggle.addEventListener('change', () => updateCompany(toggle));
});
// Сообщение после нажатия «Скачать zip» (страница не перезагружается — файл просто скачивается)
const docsForm = document.getElementById('docs-form');
const docHint  = document.getElementById('doc-hint');

docsForm.addEventListener('submit', () => {
    const selected = docsForm.querySelectorAll('input[name="docs"]:checked:enabled').length;
    if (selected === 0) return;            // ничего не выбрано — сервер вернёт на страницу
    docHint.textContent = '✔ Архив формируется — скачивание начнётся через пару секунд';
    docHint.classList.add('hint-ok');
});

// «Скачать zip»: отправляем форму сами через fetch, чтобы знать, когда архив готов
docsForm.addEventListener('submit', async (event) => {
    event.preventDefault();                          // обычную отправку отменяем

    const selected = docsForm.querySelectorAll('input[name="docs"]:checked:enabled').length;
    if (selected === 0) {
        setHint('Выберите хотя бы один документ', 'hint-error');
        return;
    }

    setHint('Архив формируется…', '');
    try {
        const response = await fetch(docsForm.action, {
            method: 'POST',
            body: new FormData(docsForm)             // те же поля, что при обычной отправке, включая _csrf
        });

        if (response.url.includes('/login-error')) { // сессия закончилась
            window.location.href = response.url;
            return;
        }
        const type = response.headers.get('Content-Type') || '';
        if (!response.ok || type.includes('text/html')) {
            throw new Error('not a file');           // вместо архива пришла страница
        }

        const blob = await response.blob();
        const fileName = fileNameFrom(response.headers.get('Content-Disposition')) || 'documents.zip';
        downloadBlob(blob, fileName);
        setHint('✔ Архив скачан: ' + fileName, 'hint-ok');
    } catch (e) {
        setHint('Не удалось сформировать архив — попробуйте ещё раз', 'hint-error');
    }
});

function setHint(text, extraClass) {
    docHint.textContent = text;
    docHint.className = 'action-bar__hint ' + extraClass;
}

// Достаём имя файла из заголовка: filename*=UTF-8''Jan_Kowalski.zip
function fileNameFrom(header) {
    if (!header) return null;
    const utf = header.match(/filename\*=UTF-8''([^;]+)/i);
    if (utf) return decodeURIComponent(utf[1]);
    const plain = header.match(/filename="?([^";]+)"?/i);
    return plain ? plain[1] : null;
}

// «Скачать» файл, который уже лежит в памяти браузера
function downloadBlob(blob, fileName) {
    const url = URL.createObjectURL(blob);           // временная ссылка на файл в памяти
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;                        // атрибут download = «сохранить с этим именем»
    document.body.appendChild(link);
    link.click();
    link.remove();
    setTimeout(() => URL.revokeObjectURL(url), 1000); // освобождаем память
}
