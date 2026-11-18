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

// Фирменное / своё жильё
const companyBlock  = document.getElementById('company-address');
const ownBlock      = document.getElementById('own-address');
const companySelect = document.getElementById('companyAddressId');
const companyAptRow = document.getElementById('company-apartment');

function updateHousing() {
    const checked = document.querySelector('input[name="companyHousing"]:checked');
    const company = checked !== null && checked.value === 'true';

    companyBlock.hidden   = !company;
    companyBlock.disabled = !company;   // выключенный блок не отправляется и не проверяется
    ownBlock.hidden       = company;
    ownBlock.disabled     = company;
}

function updateCompanyApartment() {
    const option = companySelect.selectedOptions[0];
    companyAptRow.hidden = !(option && option.dataset.needsApartment === 'true');
}

document.querySelectorAll('input[name="companyHousing"]')
    .forEach(r => r.addEventListener('change', updateHousing));
companySelect.addEventListener('change', updateCompanyApartment);

updateHousing();            // при загрузке (и после «Редактировать»)
updateCompanyApartment();

// ---------- Подсказки польских адресов (TERYT) ----------
const plCity      = document.getElementById('localAddress.city');
const plStreet    = document.getElementById('localAddress.street');
const plCommune   = document.getElementById('localAddress.commune');
const plDistrict  = document.getElementById('localAddress.district');

let citySym      = null;  // код выбранного города — нужен для поиска улиц


function cityLabel(c) {
    return c.name + ' (gm. ' + c.gmina + ', pow. ' + c.powiat + ')';
}

function streetLabel(s) {
    return s.type ? s.type + ' ' + s.name : s.name;
}


// запрос к нашему серверу; если что-то не так (например, кончилась сессия) — пустой список
async function getJson(url) {
    try {
        const response = await fetch(url);
        if (!response.ok) return [];
        return await response.json();
    } catch (e) {
        return [];
    }
}

function attachSuggestions(input, loadItems, labelOf, onChoose) {
    const box = document.createElement('ul');
    box.className = 'suggestions';
    box.hidden = true;
    input.parentElement.style.position = 'relative';
    input.after(box);                       // список сразу под полем

    let timer = null;

    input.addEventListener('input', () => {
        clearTimeout(timer);
        timer = setTimeout(async () => {
            const q = input.value.trim();
            const items = q.length < 2 ? [] : await loadItems(q);

            box.innerHTML = '';
            items.forEach(item => {
                const li = document.createElement('li');
                li.textContent = labelOf(item);
                li.addEventListener('pointerdown', e => {
                    e.preventDefault();     // чтобы поле не потеряло фокус раньше выбора
                    onChoose(item);
                    box.hidden = true;
                });
                box.appendChild(li);
            });
            box.hidden = items.length === 0;
        }, 300);
    });

    input.addEventListener('blur', () => { box.hidden = true; });   // ушли из поля — прячем
}

// --- город ---
plCity.addEventListener('input', () => { citySym = null; });     // печатают руками — код неизвестен

attachSuggestions(
    plCity,
    q => getJson('/api/pl/cities?q=' + encodeURIComponent(q)),
    cityLabel,
    c => {
        plCity.value     = c.name;
        plCommune.value  = c.gmina;
        plDistrict.value = c.powiat;
        citySym = c.sym;
    }
);

// --- улица ---
attachSuggestions(
    plStreet,
    q => citySym
        ? getJson('/api/pl/streets?sym=' + citySym + '&q=' + encodeURIComponent(q))
        : Promise.resolve([]),              // город не выбран из подсказок — улицы не ищем
    streetLabel,
    s => {
                    plStreet.value = s.name;
                    streetSym = s.symUl;
                    void fillPostcode();
                    void fillTaxOffice();
                }       // без "ul." — он уже есть в шаблонах
);
// ---------- Индекс по адресу (UUG) ----------
const plHouse  = document.getElementById('localAddress.house');
const plPostal = document.getElementById('localAddress.postalCode');

async function fillPostcode() {
    const city   = plCity.value.trim();
    const street = plStreet.value.trim();
    const house  = plHouse.value.trim();
    if (!city || !street || !house) return;

    // индекс вписан руками — не трогаем
    if (plPostal.value && plPostal.dataset.auto !== 'true') return;

    const url = '/api/pl/postcode?city=' + encodeURIComponent(city)
        + '&street=' + encodeURIComponent(street)
        + '&house='  + encodeURIComponent(house);
    try {
        const response = await fetch(url);
        const code = response.ok ? (await response.text()).trim() : '';
        if (/^\d{2}-\d{3}$/.test(code)) {
            plPostal.value = code;
            plPostal.dataset.auto = 'true';
        } else if (plPostal.dataset.auto === 'true') {
            plPostal.value = '';           // старый авто-индекс к новому адресу не подходит
        }
    } catch (e) {
        // сервис недоступен — индекс впишут руками
    }
}

plHouse.addEventListener('change', fillPostcode);
plPostal.addEventListener('input', () => { plPostal.dataset.auto = 'false'; });

// ---------- Urząd skarbowy (пока только Щецин) ----------
const SZCZECIN_SYM = '0977976';
const taxSelect = document.getElementById('taxOffice');
let streetSym = null;   // код выбранной улицы

async function fillTaxOffice() {
    if (citySym !== SZCZECIN_SYM || !streetSym) return;
    if (taxSelect.value && taxSelect.dataset.auto !== 'true') return;   // выбран руками — не трогаем

    const url = '/api/pl/tax-office?sym=' + streetSym
        + '&house=' + encodeURIComponent(plHouse.value.trim());
    try {
        const response = await fetch(url);
        const office = response.ok ? (await response.text()).trim() : '';
        if (['FIRST', 'SECOND', 'THIRD'].includes(office)) {
            taxSelect.value = office;
            taxSelect.dataset.auto = 'true';
        } else if (taxSelect.dataset.auto === 'true') {
            taxSelect.value = '';                     // старый авто-выбор к новому адресу не подходит
        }
    } catch (e) {
        // не получилось — выберут руками
    }
}

plStreet.addEventListener('input', () => { streetSym = null; });       // улицу печатают руками
plHouse.addEventListener('change', fillTaxOffice);
taxSelect.addEventListener('change', () => { taxSelect.dataset.auto = 'false'; });

// ---------- Подсказки украинских адресов (Новая Почта) ----------
const uaCity   = document.getElementById('homeAddress.city');
const uaStreet = document.getElementById('homeAddress.street');
const uaRegion = document.getElementById('homeAddress.region');
let uaCityRef  = null;   // Ref выбранного города — только если у него есть улицы

function isUkraine() {
    const checked = document.querySelector('input[name="citizenship"]:checked');
    return checked !== null && checked.value === 'UA';
}

uaCity.addEventListener('input', () => { uaCityRef = null; });   // печатают руками

attachSuggestions(
    uaCity,
    q => isUkraine()
        ? getJson('/api/ua/cities?q=' + encodeURIComponent(q))
        : Promise.resolve([]),                 // подсказки только для Украины
    c => c.present,                            // "м. Харків, Харківська обл."
    c => {
        uaCity.value   = c.name;               // "Харків"
        uaRegion.value = c.area;               // "Харківська"
        uaCityRef = c.streetsAvailable ? c.ref : null;
    }
);

attachSuggestions(
    uaStreet,
    q => uaCityRef
        ? getJson('/api/ua/streets?ref=' + uaCityRef + '&q=' + encodeURIComponent(q))
        : Promise.resolve([]),                 // нет города или у него нет улиц в базе
    s => s.type + ' ' + s.name,                // "вул. Сумська"
    s => { uaStreet.value = s.name; }          // "Сумська"
);

const birthPlace = document.getElementById('birthPlace');

attachSuggestions(
    birthPlace,
    q => isUkraine()
                ? getJson('/api/ua/cities?q=' + encodeURIComponent(q))
                : Promise.resolve([]),
    c => c.present,
    c => {birthPlace.value = c.name;}
);
