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