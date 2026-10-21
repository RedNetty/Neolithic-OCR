# Neolithic OCR

A Java desktop tool that captures a region of the screen, extracts the text with Tesseract and sends it to an OpenAI model with your instructions.

[![CI](https://github.com/RedNetty/Neolithic-OCR/actions/workflows/ci.yml/badge.svg)](https://github.com/RedNetty/Neolithic-OCR/actions/workflows/ci.yml) ![Java](https://img.shields.io/badge/java-21-blue)

Neolithic runs in the background and listens for a global hotkey. When triggered, you drag a box around any text on screen (a worksheet, a PDF, a web page), proofread the OCR result, type an instruction such as "explain this" or "solve this math problem", and get the answer in a chat window. It was written to help students with learning disabilities work with text they find hard to read.

## Features

- Global hotkey (`Ctrl` + `Shift`) that works from any application
- Click-and-drag area selection with a live preview
- OCR with Tess4J, using the bundled English model in `tessdata/`
- Text cleanup with LanguageTool spelling and grammar correction
- Editable OCR result and instruction box before anything is sent
- Chat window for follow-up questions, using `gpt-4-turbo-preview`

## Requirements

- JDK 21
- Maven
- An OpenAI API key
- A desktop session (the app uses Swing and a native keyboard hook)

## Building

```bash
mvn clean package -DskipTests
```

This is the same command CI runs. `mvn test` runs the unit tests in `src/test`.

## Configuration

The API key is read from the `OPENAI_API_KEY` environment variable:

```bash
export OPENAI_API_KEY=your-key        # Linux/macOS
set OPENAI_API_KEY=your-key           # Windows cmd
```

If the variable is not set, the app falls back to `config.json` on the classpath. Copy the example and add your key:

```bash
cp src/main/resources/config.example.json src/main/resources/config.json
```

```json
{
  "api": {
    "openai": {
      "token": "your-key"
    }
  }
}
```

`config.json` is listed in `.gitignore`.

## Usage

Run from the repository root, since the OCR model is loaded from `./tessdata`:

```bash
mvn compile exec:java -Dexec.mainClass="org.codered.neolithic.Neolithic"
```

1. Press `Ctrl` + `Shift` to start a capture. A preview window opens.
2. Click and drag over the area you want. The preview updates when you release the mouse.
3. Click **Accept** (or press the hotkey again). Press `Esc` or close the preview to cancel.
4. In the "Converted Text" dialog, correct the OCR text if needed and enter an instruction. **Refresh Text** runs OCR on the image again.
5. Click **Send to AI**. The response appears in a chat window, where you can ask follow-up questions.

Closing the chat window exits the application.

## Project structure

```
src/main/java/org/codered/neolithic/
  Neolithic.java          entry point
  screenshot/             global hotkey and screen area selection
  images/conversion/      Tesseract OCR
  images/processing/      text cleanup, spelling and grammar correction
  images/ui/              converted text dialog
  openai/                 OpenAI request and chat window
  utils/                  API key lookup (environment variable or config.json)
tessdata/                 English Tesseract model
```
