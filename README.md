# Neolithic AI Tool

## Overview
Neolithic AI Tool is a Java-based application designed for screen capturing with additional processing capabilities. It features a Window Capture Tool that allows users to select and capture specific areas of their screen, potentially for further image processing or analysis.

### Key Features
- **Screen Capture**: Capture any part of your screen with a simple and intuitive interface.
- **Area Selection**: Select specific areas for capture using your mouse.
- **Preview Functionality**: Preview the captured area before finalizing the capture.
- **Extendable**: Designed to be easily extendable for additional image processing tasks.

## Installation
To use the Neolithic AI Tool, you'll need to have Java installed on your machine. Follow these steps to set up the tool:

1. Clone the repository or download the source code.
2. Open the project in your favorite Java IDE (like Eclipse, IntelliJ IDEA, or NetBeans).
3. Ensure you have the latest Java Development Kit (JDK) installed.
4. Build and run the application from the IDE.

## Usage
Once you run the application, it initializes and waits for a capture command. 

To capture a screen area:
1. Press `Ctrl + PrintScreen`.
2. Select the area of the screen you want to capture.
3. A preview window will appear, showing the selected area.
4. Confirm the capture, or cancel to start over.

## Configuring Google Cloud Authentication
To utilize the Google Vision integration in the Neolithic AI Tool, you need to authenticate with Google Cloud Platform. This is done through a `service_account.json` file, which contains your Google Cloud credentials.

### Step-by-Step Guide:
1. **Create a Google Cloud Project**:
   - Sign up or log in at [Google Cloud Console](https://console.cloud.google.com/).
   - Create a new project.

2. **Enable Google Vision API**:
   - In your Google Cloud project, navigate to "API & Services" dashboard.
   - Enable the Google Vision API for your project.

3. **Create a Service Account**:
   - Go to "IAM & Admin" > "Service Accounts".
   - Create a new service account and assign necessary roles (e.g., Vision API User).

4. **Generate `service_account.json` Key**:
   - Click on the created service account.
   - Go to "Keys", add a new key, select "JSON" and download it.

5. **Add the JSON File to Your Project**:
   - Rename the downloaded file to `service_account.json`.
   - Place it in the root directory of your Neolithic AI Tool project.
   - Add this file to your `.gitignore` to keep it secure.

6. **Update Application Configuration**:
   - Set up your application to use the `service_account.json` for authentication.

### Important Notes:
- Keep your `service_account.json` file secure and never expose it publicly.
- Regularly review your service account permissions and audit your Google Cloud usage.

## Contributing
Contributions to Neolithic AI Tool are welcome. Open an issue or a pull request for ideas for improvements or any issues encountered.

## License
Neolithic AI Tool is open-source software licensed under the MIT license. See the LICENSE file for more details.
