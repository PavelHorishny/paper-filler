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

## Adding a document template

1. Put the `.docx` file into `templates/`.
2. Use placeholders `{{fieldName}}` in the document — the name must match the key in
   `DocumentGenerator.buildData()` (by convention, the field name in `ClientForm`).
   Type each placeholder in one go so Word does not split it.
3. Add an entry to `config/documents.yaml`:

```yaml
- { id: karta, template: "Informacja (karta) APT.docx", required: false,
    output-name: "{{surname}}_{{name}}_karta_{{fulfilmentDate}}" }
```

On startup the app checks that every template from `documents.yaml` exists.

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