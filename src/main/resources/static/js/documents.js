function updateCompany(toggle) {
    const box = document.querySelector('fieldset[data-company="' + toggle.value + '"]');
    box.hidden = !toggle.checked;
    box.disabled = !toggle.checked;   // выключенный блок не отправляется на сервер
}

document.querySelectorAll('.company-toggle').forEach(toggle => {
    updateCompany(toggle);   // при загрузке: браузер мог запомнить галочку после «Назад»
    toggle.addEventListener('change', () => updateCompany(toggle));
});
