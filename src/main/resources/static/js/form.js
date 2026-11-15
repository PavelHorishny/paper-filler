const birthDate   = document.getElementById('birthDate');
const studentRow  = document.getElementById('student-row');
const studentBox  = document.querySelector('input[type="checkbox"][name="student"]');
const schoolBlock = document.getElementById('school-block');

// Полных лет на сегодня. Дата приходит из поля как "1990-05-17"
function age(isoDate) {
    const [y, m, d] = isoDate.split('-').map(Number);
    const now = new Date();
    const years = now.getFullYear() - y;
    const birthdayPassed = now.getMonth() + 1 > m ||
        (now.getMonth() + 1 === m && now.getDate() >= d);
    return birthdayPassed ? years : years - 1;
}

function update() {
    const young = birthDate.value !== '' && age(birthDate.value) < 26;
    studentRow.hidden = !young;
    if (!young) studentBox.checked = false;
    schoolBlock.hidden = !studentBox.checked;
}

birthDate.addEventListener('change', update);
studentBox.addEventListener('change', update);
update();

const seriesInput = document.getElementById('passportSeries');
const numberInput = document.getElementById('passportNumber');
const citizenshipRadios = document.querySelectorAll('input[name="citizenship"]');

function lengthFrom(pattern){
    const match = pattern.match(/\{(\d+)\}/);
    return match ? Number(match[1]):null;
}
function applyPassportRules(radio){
    const seriesPattern = radio.dataset.series;
    const numberPattern = radio.dataset.number;

    seriesInput.pattern = seriesPattern;
    numberInput.pattern = numberPattern;

    const letters = lengthFrom(seriesPattern);
    const digits = lengthFrom(numberPattern);
    seriesInput.maxLength = letters;
    numberInput.maxLength = digits;
    seriesInput.title = 'Серия: ' + letters+ ' латиница';
    numberInput.title = 'Номер: ' + digits + ' цифр';
}

citizenshipRadios.forEach(radio=>{
    radio.addEventListener('change', ()=>applyPassportRules(radio));
    if(radio.checked) applyPassportRules(radio);
});

seriesInput.addEventListener('input', ()=>{
    seriesInput.value = seriesInput.value.toUpperCase();
});

function  formatAccount(digits){
    let result = digits.slice(0,2);
    for (let i=2; i<digits.length; i+=4){
        result+=' '+digits.slice(i,i+4);
    }
    return result;
}
const accountInput = document.querySelector('input[name="account"]');
const bankInput = document.getElementById('bank');

accountInput.addEventListener('input', ()=>{
    const caret = accountInput.selectionStart;
    const digitsBeforeCaret = accountInput.value.slice(0,caret).replace(/\D/g,'').length;

    const digits =accountInput.value.replace(/\D/g,'').slice(0,26);
    accountInput.value = formatAccount(digits);

    let pos = 0;
    let seen = 0;
    while(pos<accountInput.value.length && seen<digitsBeforeCaret){
        if(/\d/.test(accountInput.value[pos])) seen++;
        pos++;
    }
    accountInput.setSelectionRange(pos,pos);
});

accountInput.value = formatAccount(accountInput.value.replace(/\D/g,''));

accountInput.addEventListener('input', async () => {
    const digits = accountInput.value.replace(/\s/g, '');
    if (digits.length !== 26) return;                       // ждём полный номер

    const typedByHand = bankInput.value.trim() !== '' && bankInput.dataset.auto !== 'true';
    if (typedByHand) return;                                // банк вписан руками — не трогаем

    const response = await fetch('/api/bank?account=' + digits);
    const name = await response.text();
    if (name) {
        bankInput.value = name;
        bankInput.dataset.auto = 'true';                    // запомнили: заполнено автоматически
    }
});

bankInput.addEventListener('input', () => {
    bankInput.dataset.auto = 'false';                       // пользователь правит сам
});

const whatsNew = document.getElementById('whatsNew');
if (whatsNew) {
    const version = whatsNew.dataset.version;

    let seen = null;
    try {
        seen = localStorage.getItem('seenVersion');
    } catch (e) {
        // localStorage может быть недоступен (приватный режим) — тогда просто покажем окно
    }

    if (seen !== version) {
        whatsNew.showModal();
    }

    document.getElementById('whatsNewClose')
        .addEventListener('click', () => whatsNew.close());

    // 'close' срабатывает и от кнопки, и от клавиши Esc
    whatsNew.addEventListener('close', () => {
        try {
            localStorage.setItem('seenVersion', version);
        } catch (e) {}
    });
}