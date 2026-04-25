document.addEventListener("DOMContentLoaded", () => {

    const editor = document.getElementById("editor");
    const resultado = document.getElementById("resultado");
    const tabla = document.getElementById("tbodyErrores");
    const fileInput = document.getElementById("archivoSQL");
    const contador = document.getElementById("contadorErrores");

    document.getElementById("btnEjecutar").addEventListener("click", () => {

        const operacion = editor.innerText;

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

    document.getElementById("btnSubir").addEventListener("click", () => {
        fileInput.click();
    });

    fileInput.addEventListener("change", () => {

        const archivo = fileInput.files[0];
        if (!archivo) return;

        const reader = new FileReader();

        reader.onload = e => {
            editor.innerText = e.target.result;
            actualizarLineas();
        };

        reader.readAsText(archivo);

    });

    function manejarRespuesta(data) {

        const resultado = document.getElementById("resultado");
        const tabla = document.getElementById("tbodyErrores");
        const contador = document.getElementById("contadorErrores");

        resultado.innerText = "";
        tabla.innerHTML = "";

        if (data.includes("ERROR:")) {
            llenarTablaErrores(data);
        } else {
            pintarTablaResultado(data);
        }
    }

    function llenarTablaErrores(texto) {

        const tabla = document.getElementById("tbodyErrores");
        const contador = document.getElementById("contadorErrores");

        tabla.innerHTML = "";

        const lineas = texto.split("\n");
        let total = 0;

        lineas.forEach(linea => {

            linea = linea.trim();

            if (!linea.startsWith("ERROR:")) return;
            if (linea.includes("recuperado")) return;

            total++;

            let tipo = "";
            let fila = "-";
            let columna = "-";
            let valor = "-";

            if (linea.includes("Lexico")) {
                tipo = "Error Léxico";

                const m = linea.match(/linea:\s*(-?\d+).*columna:\s*(-?\d+).*valor:\s*'(.+)'/);
                if (m) {
                    fila = m[1];
                    columna = m[2];
                    valor = m[3];
                } else {
                    // Si no coincide el regex, extraer lo que se pueda
                    const partes = linea.split("|");
                    if (partes.length > 1) {
                        valor = partes[partes.length].trim();
                    }
                }
            }

            if (linea.includes("Sintactico")) {
                tipo = "Error Sintáctico";

                const m = linea.match(/linea:\s*(-?\d+).*columna:\s*(-?\d+).*token:\s*(.+)/);
                if (m) {
                    fila = m[1];
                    columna = m[2];
                    valor = m[3];
                } else {
                    // Si no coincide el regex, extraer lo que se pueda
                    const partes = linea.split("|");
                    if (partes.length > 1) {
                        valor = partes[partes.length - 1].trim();
                    }
                }
            }

            if (linea.includes("Ejecucion")) {
                tipo = "Error de Ejecución";
                
                const m = linea.match(/ERROR: Ejecucion \| (.+)/);
                if (m) {
                    valor = m[1];
                }
            }

            const row = document.createElement("tr");

            row.innerHTML = `
            <td>${total}</td>
            <td>${fila}</td>
            <td>${columna}</td>
            <td>${valor}</td>
            <td>${tipo}</td>
        `;

            tabla.appendChild(row);

        });

        contador.innerText = "Errores: " + total;
    }

    cargarSidebar();

    function cargarSidebar() {

        fetch("../backend/listar_db.php")
            .then(res => res.json())
            .then(data => pintarSidebar(data));
    }

    function pintarSidebar(data) {

        const nav = document.querySelector(".sidebar-nav");
        nav.innerHTML = "";

        data.forEach(db => {

            const btnDB = document.createElement("button");
            btnDB.className = "nav-item active";

            btnDB.innerHTML = `
            <span class="material-symbols-outlined" style="font-variation-settings:'FILL' 1;">folder</span>
            ${db.db}
        `;

            nav.appendChild(btnDB);

            db.tablas.forEach(tabla => {

                const btnTabla = document.createElement("button");
                btnTabla.className = "nav-item";

                btnTabla.innerHTML = `
                <span class="material-symbols-outlined">description</span>
                ${tabla}
            `;

                nav.appendChild(btnTabla);

            });

        });

    }

    function pintarTablaResultado(texto) {

        const resultado = document.getElementById("resultado");
        const contador = document.getElementById("contadorErrores");

        resultado.innerHTML = "";

        const lineas = texto.split("\n").filter(l => l.trim() !== "");

        if (lineas.length === 0) return;

        // Crear tabla HTML dinámicamente
        let htmlTabla = "<table style='width: 100%; border-collapse: collapse;'>";
        
        // Headers
        const columnas = lineas[0].split(",");
        htmlTabla += "<thead><tr>";
        columnas.forEach(col => {
            htmlTabla += `<th style='border: 1px solid #ddd; padding: 8px; text-align: left;'>${col.trim()}</th>`;
        });
        htmlTabla += "</tr></thead>";

        // Filas
        htmlTabla += "<tbody>";
        for (let i = 1; i < lineas.length; i++) {

            const fila = parseCSV(lineas[i]);
            htmlTabla += "<tr>";

            fila.forEach(valor => {
                htmlTabla += `<td style='border: 1px solid #ddd; padding: 8px;'>${valor.replace(/^'|'$/g, "")}</td>`;
            });

            htmlTabla += "</tr>";
        }

        htmlTabla += "</tbody></table>";
        resultado.innerHTML = htmlTabla;

        contador.innerText = "Filas: " + (lineas.length - 1);
    }

    function parseCSV(linea) {

        const resultado = [];
        let actual = "";
        let enComillas = false;

        for (let i = 0; i < linea.length; i++) {

            const char = linea[i];

            if (char === "'") {
                enComillas = !enComillas;
                actual += char;
            }
            else if (char === "," && !enComillas) {
                resultado.push(actual.trim());
                actual = "";
            }
            else {
                actual += char;
            }
        }

        resultado.push(actual.trim());

        return resultado;
    }

    const gutter = document.querySelector(".gutter");

    function actualizarLineas() {

        const texto = editor.innerText || "";
        const lineas = texto.split("\n");

        gutter.innerHTML = "";

        for (let i = 0; i < lineas.length; i++) {
            const span = document.createElement("span");
            span.innerText = i + 1;
            gutter.appendChild(span);
        }
    }

    // escribir en el editor
    editor.addEventListener("input", actualizarLineas);

    // sincronizar scroll
    editor.addEventListener("scroll", () => {
        gutter.scrollTop = editor.scrollTop;
    });

    // iniciar con 1 
    actualizarLineas();
});