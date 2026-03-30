# Neolithic OCR

**Screen-capture OCR tool with AI-powered text analysis, built in Java.**

Neolithic is a desktop utility that lets you capture any region of your screen, extract text from it using Tesseract OCR, and then optionally send that text to GPT-4 for analysis, summarization, or Q&A — all without leaving your workflow.

## Features

- **Global hotkey capture** — Press `Ctrl + Shift` from any application to trigger the capture overlay
- **Freehand area selection** — Click and drag to select exactly the region you want
- **Live preview** — See the selected area before confirming
- **Tesseract OCR** — Accurate text extraction from screenshots using the bundled `eng.traineddata` model
- **GPT-4 integration** — Send extracted text to OpenAI for analysis with a custom prompt
- **Interactive chat** — Continue the conversation with the AI in a built-in chat window
- **Image preprocessing** — Converts and processes captured images for better OCR accuracy

## Tech Stack

- **Java 17**
- **Swing** (desktop UI)
- **Tesseract / Tess4J** (OCR)
- **OpenAI Java SDK** (GPT-4 API)
- **JNativeHook** (global keyboard shortcuts)
- **Maven**

## Setup

### Prerequisites

- Java 17+
- Maven 3.8+
- An [OpenAI API key](https://platform.openai.com/api-keys)

### Configuration

**Option 1: Environment variable (recommended)**

Set `OPENAI_API_KEY` in your environment:

```bash
# Linux/macOS
export OPENAI_API_KEY=sk-your-key-here

# Windows
set OPENAI_API_KEY=sk-your-key-here
```

**Option 2: Config file**

```bash
cp src/main/resources/config.example.json src/main/resources/config.json
# Edit config.json and add your API key
```

> ⚠️ Never commit `config.json` — it's excluded by `.gitignore`.

### Build & Run

```bash
mvn clean install
mvn exec:java -Dexec.mainClass="org.codered.neolithic.Neolithic"
```

## Usage

1. Launch the application
2. Press **`Ctrl + Shift`** anywhere on your screen
3. Click and drag to select the area you want to capture
4. Review the preview — click **Accept** to proceed
5. Neolithic runs OCR on the selection and opens a chat window
6. The extracted text is automatically sent to GPT-4 with a default prompt
7. Continue the conversation to ask follow-up questions about the captured content

## Project Structure

```
src/main/java/org/codered/neolithic/
├── customize/          # Configuration management
├── images/             # OCR conversion, image preprocessing, UI
│   ├── conversion/     # Image-to-text pipeline
│   ├── processing/     # Image manipulation utilities
│   └── ui/             # Conversion result UI
├── openai/             # OpenAI API handler and request models
├── screenshot/         # Screen capture tool and hotkey listener
├── utils/              # Config reader (env + JSON)
└── Neolithic.java      # Application entry point
```

## License

Open-source under the MIT license.
