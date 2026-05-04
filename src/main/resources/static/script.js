document.getElementById("uploadForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const fileInput = document.getElementById("fileInput");
    const formData = new FormData();
    formData.append("file", fileInput.files[0]);

    const resultBox = document.getElementById("resultBox");
    resultBox.textContent = "⏳ Uploading and parsing...";

    try {
        const response = await fetch("/api/parse-pdf", {
            method: "POST",
            body: formData,
        });

        const result = await response.json();

        if (!response.ok) {
            resultBox.textContent = "❌ " + (result.error || JSON.stringify(result));
            return;
        }

        resultBox.textContent = JSON.stringify(result, null, 2);
    } catch (err) {
        resultBox.textContent = "❌ Error: " + err.message;
    }
});
