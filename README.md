# Paper Filler

Web application that fills Word (.docx) templates with client data entered in a form.
The user fills in the form once, picks the needed documents and downloads them ready to print.

## Features

- Client form: personal data, passport, PESEL, bank account, home and Polish address, tax office
- Automatic transliteration of Cyrillic (Ukrainian, Belarusian) into Latin
- Preview screen with "Edit" / "New form" before generating documents
- Generation of selected .docx templates, downloaded as a zip
- Google sign-in with an allowlist of emails

## Tech stack

Java 21 · Spring Boot 4 · Thymeleaf · poi-tl (Word templates) · Lombok ·
Spring Security OAuth2 (Google) · Docker · Google Cloud Run

## Project structure

```
src/main/java/app/paper_filler/
├── config/      app settings, config validation, global error handling
├── security/    Google sign-in and email allowlist
├── form/        client form, session, address preparation, validation
├── documents/   document generation and download
├── translit/    Cyrillic → Latin transliteration
└── taxoffice/   tax offices
config/documents.yaml   list of templates
templates/              .docx templates
```

## Running locally

Requirements: JDK 21, a Google OAuth client (Google Cloud Console → Credentials).

Set environment variables (in IntelliJ: Run → Edit Configurations → Environment variables):

| Variable | Description |
|---|---|
| `GOOGLE_CLIENT_ID` | OAuth client ID |
| `GOOGLE_CLIENT_SECRET` | OAuth client secret |
| `ALLOWED_EMAILS` | comma-separated emails allowed to sign in |

Then run:

```bash
./mvnw spring-boot:run
```

Open http://localhost:8080. The redirect URI `http://localhost:8080/login/oauth2/code/google`
must be added to the OAuth client.

Run tests:

```bash
./mvnw test
```

## Templates (Cloud Storage)

Templates and `documents.yaml` are **not stored in this repository**.
They live in the Cloud Storage bucket `paper-filler-templates-962102822307`,
which Cloud Run mounts read-only at `/app/templates`.

Locally the same files are kept in `templates/` (ignored by git):

```
templates/
├── documents.yaml        list of documents
├── *.docx                common documents
├── APT/*.docx            company APT
└── OUT/*.docx            company OUT
```

### Update an existing template

1. Edit the `.docx` locally in `templates/`, check generation locally.
2. Cloud Console → Cloud Storage → bucket → open the folder → **Upload** → replace the file
   (same name and same folder).
3. Done — the new version is used within about a minute, no restart needed.

## Templates (Cloud Storage)

Templates and `documents.yaml` are **not stored in this repository**.
They live in the Cloud Storage bucket `paper-filler-templates-962102822307`,
which Cloud Run mounts read-only at `/app/templates`.

Locally the same files are kept in `templates/` (ignored by git):

```
templates/
├── documents.yaml        list of documents
├── *.docx                common documents
├── APT/*.docx            company APT
└── OUT/*.docx            company OUT
```

### Update an existing template

1. Edit the `.docx` locally in `templates/`, check generation locally.
2. Cloud Console → Cloud Storage → bucket → open the folder → **Upload** → replace the file
   (same name and same folder).
3. Done — the new version is used within about a minute, no restart needed.

### Add a new template

1. Put placeholders `{{key}}` into the `.docx`. Keys must exist in `DocumentGenerator.buildData()`.
2. Save it to `templates/` (or `templates/APT/`, `templates/OUT/`).
3. Add an entry to `templates/documents.yaml`:
   ```yaml
   - { id: MyDoc-apt, company: APT, template: APT/MyDoc_APT.docx, required: false,
       output-name: "{{name}}_{{surname}}_MyDoc_APT_{{fulfilmentDate}}" }
   ```
   - `id` — unique, no spaces
   - `company` — `APT`, `OUT`, or omit for a common document
4. Restart the app locally and check the log: `Loaded N documents`.
5. Upload **both** the `.docx` and `documents.yaml` to the bucket.
6. Restart the service so it re-reads `documents.yaml` (Cloud Shell):
   ```bash
   gcloud run services update paper-filler --region europe-central2 --update-labels restart=$(date +%s)
   ```
7. Check the Cloud Run log: `Loaded N documents`.

### Remove a template

1. Delete its entry from `templates/documents.yaml` **first**.
2. Upload the updated `documents.yaml` to the bucket.
3. Restart the service (command above).
4. Only now delete the `.docx` from the bucket and from `templates/`.

> Order matters: on startup the app checks that every template listed in
> `documents.yaml` exists. If the file is deleted while it is still listed,
> the app will not start.

### Get the templates locally (new machine)

Cloud Console → bucket → select files → **Download**, put them into `templates/`
keeping the folder structure.
### Remove a template

1. Delete its entry from `templates/documents.yaml` **first**.
2. Upload the updated `documents.yaml` to the bucket.
3. Restart the service (command above).
4. Only now delete the `.docx` from the bucket and from `templates/`.

> Order matters: on startup the app checks that every template listed in
> `documents.yaml` exists. If the file is deleted while it is still listed,
> the app will not start.

### Get the templates locally (new machine)

Cloud Console → bucket → select files → **Download**, put them into `templates/`
keeping the folder structure.
## Deployment (Cloud Run)

Deployed from `master` to Google Cloud Run (region `europe-central2`).
The client secret is stored in Secret Manager.

```bash
git pull
gcloud run deploy paper-filler --source . --region europe-central2
```

Environment variables and secrets are kept between deployments.
Logs: Cloud Run → paper-filler → Logs.

## Workflow and versioning

- One branch per task, merged into `master` via pull request
- Versions follow [SemVer](https://semver.org/) and are tagged as `vX.Y.Z` with a GitHub Release
