<?php

chdir(__DIR__);

function ejecutar($archivo) {

    $archivo = realpath($archivo);
    $build = __DIR__ . "\\build.bat";
    $base = __DIR__;

    if (!$archivo) {
        return "ERROR: No se encontro el archivo";
    }

    if (!file_exists($build)) {
        return "ERROR: No se encontro build.bat";
    }

    $archivoRelativo = basename($archivo);
    $cmd = 'cmd /c "cd /d "' . $base . '" && build.bat "' . $archivoRelativo . '" 2>&1"';

    $output = [];
    $return_var = 0;

    exec($cmd, $output, $return_var);

    $salida = implode("\n", $output);

    if (empty(trim($salida))) {
        return "ERROR: No se pudo ejecutar";
    }

    return trim($salida);
}


if (!empty($_FILES['archivo'])) {

    if ($_FILES['archivo']['error'] !== 0) {
        echo "ERROR: Error al subir archivo";
        exit;
    }

    $ruta = __DIR__ . "\\entrada_upload.sql";

    if (!move_uploaded_file($_FILES['archivo']['tmp_name'], $ruta)) {
        echo "ERROR: No se pudo guardar el archivo";
        exit;
    }

    echo ejecutar($ruta);
    exit;
}


if (isset($_POST['operacion'])) {

    $ruta = __DIR__ . "\\entrada.sql";

    file_put_contents($ruta, $_POST['operacion']);

    echo ejecutar($ruta);
    exit;
}


echo "ERROR: Entrada no valida";
