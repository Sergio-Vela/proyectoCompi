<?php

chdir(__DIR__);

function ejecutar($archivo) {

    $archivo = realpath($archivo);

    if (!$archivo) {
        return "ERROR: No se encontro el archivo";
    }

    $SRC = __DIR__ . "\\src";
    $OUT = __DIR__ . "\\out";
    $CUP = "C:\\Compiladores\\tools\\java-cup-11b.jar";

    if (!is_dir($OUT)) {
        mkdir($OUT, 0777, true);
    }

    $cmd = 'cmd /c "cd /d "' . $SRC . '" && javac -cp ".;' . $CUP . '" -d "' . $OUT . '" *.java db_logic\\*.java && java -cp "' . $OUT . ';' . $CUP . '" Main "' . $archivo . '" 2>&1"';

    $output = [];
    $return_var = 0;

    exec($cmd, $output, $return_var);

    $salida = implode("\n", $output);

    if (empty($salida)) {
        return "ERROR: No se pudo ejecutar";
    }

    $lineas = explode("\n", $salida);
    $errores = [];

    foreach ($lineas as $linea) {
        $linea = trim($linea);

        if ($linea === "") continue;

        if (strpos($linea, "ERROR:") !== false) {
            $errores[] = $linea;
        }
    }

    if (count($errores) > 0) {
        return implode("\n", $errores);
    }

    return $salida;
}


if (!empty($_FILES['archivo'])) {

    if ($_FILES['archivo']['error'] !== 0) {
        echo "ERROR: Error al subir archivo";
        exit;
    }

    $ruta = __DIR__ . "\\entrada.sql";

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
