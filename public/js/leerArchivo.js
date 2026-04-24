document.addEventListener("DOMContentLoaded", () => {

    document.getElementById("btnEjecutar").addEventListener("click", function () {

        const operacion = document.getElementById("inputOperacion").value;

        fetch("../backend/analizar.php", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: "operacion=" + encodeURIComponent(operacion)
        })
            .then(res => res.text())
            .then(data => manejarRespuesta(data));

    });


    document.getElementById("btnSubir").addEventListener("click", function () {

        const archivo = document.getElementById("archivoSQL").files[0];

        if (!archivo) {
            alert("Seleccione un archivo");
            return;
        }

        const formData = new FormData();
        formData.append("archivo", archivo);

        fetch("../backend/analizar.php", {
            method: "POST",
            body: formData
        })
            .then(res => res.text())
            .then(data => manejarRespuesta(data));

    });

});


function manejarRespuesta(data) {

    console.log("RESPUESTA BACKEND:\n", data);

    const textarea = document.getElementById("resultado");
    const tabla = document.querySelector("#tablaErrores tbody");

    if (!tabla) {
        console.error("No se encontró tbody");
        return;
    }

    textarea.value = "";
    tabla.innerHTML = "";

    if (data.includes("ERROR:")) {
        llenarTablaErrores(data);
    } else {
        textarea.value = data;
    }
}


function llenarTablaErrores(texto) {

    const tabla = document.getElementById("tbodyErrores");

    if (!tabla) {
        console.error("tbodyErrores no existe");
        return;
    }

    tabla.innerHTML = "";

    const lineas = texto.split("\n");

    let contador = 1;

    lineas.forEach(linea => {

        linea = linea.trim();

        if (!linea.startsWith("ERROR:")) return;

        let tipo = "";
        let fila = "-";
        let columna = "-";
        let valor = "-";

        if (linea.includes("Lexico")) {
            tipo = "Error Léxico";

            const match = linea.match(/linea:\s*(\d+).*columna:\s*(\d+).*valor:\s*'(.+)'/);

            if (match) {
                fila = match[1];
                columna = match[2];
                valor = match[3];
            }
        }

        if (linea.includes("Sintactico") && !linea.includes("recuperado")) {
            tipo = "Error Sintáctico";

            const match = linea.match(/linea:\s*(\d+).*columna:\s*(\d+).*token:\s*(.+)/);

            if (match) {
                fila = match[1];
                columna = match[2];
                valor = match[3];
            }
        }

        if (linea.includes("Ejecucion")) {
            tipo = "Error de Ejecución";

            const match = linea.match(/ERROR:\s*Ejecucion\s*\|\s*(.+)/);

            if (match) {
                valor = match[1];
            }
        }

        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${contador++}</td>
            <td>${fila}</td>
            <td>${columna}</td>
            <td>${valor}</td>
            <td>${tipo}</td>
        `;

        tabla.appendChild(row);

    });

}