<?php

$ruta = __DIR__ . "/db";

if (!is_dir($ruta)) {
    echo json_encode([]);
    exit;
}

$resultado = [];

$carpetas = scandir($ruta);

foreach ($carpetas as $carpeta) {

    if ($carpeta === "." || $carpeta === "..") continue;

    $rutaCarpeta = $ruta . "/" . $carpeta;

    if (is_dir($rutaCarpeta)) {

        $tablas = [];

        $archivos = scandir($rutaCarpeta);

        foreach ($archivos as $archivo) {
            if (pathinfo($archivo, PATHINFO_EXTENSION) === "txt") {
                $tablas[] = $archivo;
            }
        }

        $resultado[] = [
            "db" => $carpeta,
            "tablas" => $tablas
        ];
    }
}

header('Content-Type: application/json');
echo json_encode($resultado);