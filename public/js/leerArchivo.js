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
            .then(data => manejarRespuesta(data))
            .catch(error => mostrarErrorDeConexion(error));
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
            .then(data => manejarRespuesta(data))
            .catch(error => mostrarErrorDeConexion(error));
    });
});

function manejarRespuesta(data) {
    console.log("RESPUESTA BACKEND:\n", data);

    const textarea = document.getElementById("resultado");
    const tabla = document.querySelector("#tablaErrores tbody");

    if (!tabla) {
        console.error("No se encontro tbody");
        return;
    }

    textarea.value = data;
    tabla.innerHTML = "";

    if (data.includes("ERROR:")) {
        llenarTablaErrores(data);
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

        if (!linea.startsWith("ERROR:")) {
            return;
        }

        let tipo = "";
        let fila = "-";
        let columna = "-";
        let valor = "-";
        let detalle = "";

        if (linea.includes("Lexico")) {
            tipo = "Error Léxico";

            const match = linea.match(/linea:\s*(\d+)\s*\|\s*columna:\s*(\d+)\s*\|\s*valor:\s*'([^']*)'(?:\s*\|\s*detalle:\s*(.+))?/);

            if (match) {
                fila = match[1];
                columna = match[2];
                valor = match[3];
                detalle = match[4] || "Caracter no reconocido";
            }
        }

        if (linea.includes("Sintactico") && !linea.includes("recuperado")) {
            tipo = "Error Sintáctico";

            const match = linea.match(/linea:\s*(\d+)\s*\|\s*columna:\s*(\d+)\s*\|\s*token:\s*(.*?)\s*\|\s*detalle:\s*(.+)/);

            if (match) {
                fila = match[1];
                columna = match[2];
                valor = match[3];
                detalle = match[4];
            }
        }

        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${contador++}</td>
            <td>${fila}</td>
            <td>${columna}</td>
            <td>${valor}</td>
            <td>${detalle ? `${tipo}: ${detalle}` : tipo}</td>
        `;

        tabla.appendChild(row);
    });
}

function mostrarErrorDeConexion(error) {
    console.error(error);

    const textarea = document.getElementById("resultado");
    const tabla = document.querySelector("#tablaErrores tbody");

    if (tabla) {
        tabla.innerHTML = "";
    }

    if (textarea) {
        textarea.value = "ERROR: No se pudo conectar con el backend";
    }
}
