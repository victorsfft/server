# Contexto del proyecto

Este proyecto trabaja junto con otro proyecto ubicado en:
- Servidor: `C:\Users\victo\OneDrive\Documentos\GitHub\server`
- Cliente y javaFx: `C:\Users\victo\OneDrive\Documentos\GitHub\javafx`

Cuando necesites trabajar con archivos del otro proyecto, usa estas rutas.

Siempre debes de seguir la nomenclatura de los archivos y la forma en la que estan escritos ========================================

DIÁLOGOS ALERTA

======================================== */
los metodos y atributos.
Ademas mantén siempre la misma estructura para los mensajes de los handlers y sus cases.


reducir el código su es posible y los estilos con mayor cantidad de líneas aplicarlo desde un css.

Evita los comentarios. menos los que agrupan los métodos con comentarios como:

/* 
Mantener la nomenclatura en todos los métodos,variables y comentarios, ejemplos:
mostrarConfirmacion
mostrarError
crearLabelFormulario
crearGridFormulario
calendarioPane
departamentosGrupo
fechaActualLabel.

Y mirar que los nombres de los métodos no sean demasiado detallados para facilitar la compresión del método con solo leerlo.

Poner espacios entre métodos, y bloques de código.

Ademas tienes que organizar el código por bloques como:
passwordField = new PasswordField();
mostrandoContraseña = false;

textField = new TextField();
textField.setVisible(false);
textField.setManaged(false);

mostrarButton = new Button();
mostrarButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 4;");
mostrarButton.setFocusTraversable(false);

StackPane campoStack = new StackPane(passwordField, textField);
StackPane.setAlignment(mostrarButton, Pos.CENTER_RIGHT);
StackPane.setMargin(mostrarButton, new Insets(0, 8, 0, 0));

se agrupan las cosas según el elemento que se modifique.


Hacer los diseños responsivos y con el diseño igual que otros elementos parecidos.

Si tienes que hacer referencia a una clase importalo y asi no tienes que poner la ruta entera en el codigo. Si encuentras una ruta de un import pues quitalo, ejemplo:
java.time.LocalDate

Si encuentras en el codigo un import de algo, quitalo y ponlo en la cabecera.

Asegurate de que todas las letras tengan el mismo estilo.

Manten la consistencia.