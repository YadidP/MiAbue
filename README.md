👵💊 MiAbue: Tu Asistente de Medicamentos 💊👵
MiAbue es una aplicación móvil Android diseñada para ayudar a nuestros adultos mayores a gestionar sus medicamentos de forma sencilla y segura, ¡además de ofrecer una herramienta de emergencia rápida! Desarrollada con ☕ Java en Android Studio, esta app busca simplificar la adherencia a tratamientos y brindar tranquilidad a toda la familia.

✨ Características Principales
Recordatorios Personalizados: 🔔 ¡Nunca más una dosis olvidada! La app envía notificaciones puntuales para cada medicamento.

Gestión de Tratamientos: 📝 Visualiza y actualiza fácilmente toda la información de tus medicinas (dosis, frecuencia).

Botón de Emergencia: 🆘 Acceso rápido y directo a una llamada predefinida para situaciones críticas. ¡Tu seguridad es lo primero!

Interfaz Intuitiva: 👍 Diseño simple y amigable, pensado especialmente para la comodidad de los adultos mayores.

🛠️ Estructura del Proyecto
El proyecto sigue una arquitectura modular y organizada, ideal para Android:

├── app/
│   ├── java/
│   │   └── com/miabue/app/
│   │       ├── adapters/            <-- Adaptadores para listas (ej. medicamentos)
│   │       ├── database/            <-- Gestión de base de datos SQLite
│   │       ├── models/              <-- Modelos de datos (ej. Tratamiento)
│   │       ├── utils/               <-- Utilidades (ej. notificaciones)
│   │       ├── MainActivity.java    <-- Pantalla principal
│   │       ├── MedicationActivity.java <-- Ver y gestionar medicamentos
│   │       ├── NotificationReceiver.java <-- Recibe notificaciones de alarma
│   │       └── TreatmentActivity.java    <-- Registrar nuevos tratamientos
│   └── res/                         <-- Recursos (layouts, drawables, etc.)
└── build.gradle (Project)
🚀 Componentes Clave (Un vistazo rápido)
AndroidManifest.xml: Declara permisos (llamadas, notificaciones) y componentes esenciales de la app.

DatabaseHelper.java: Maneja las operaciones de guardado, lectura, actualización y eliminación de tratamientos en SQLite.

Treatment.java: El modelo de datos que representa cada tratamiento médico.

NotificationHelper.java: Se encarga de programar y cancelar las alarmas para las notificaciones de medicamentos.

NotificationReceiver.java: Activa y muestra las notificaciones cuando es hora de tomar la medicación.

MainActivity.java: La pantalla principal con un resumen diario, próximos medicamentos y botones de acceso rápido.

MedicationAdapter.java: Adapta los datos de los tratamientos para mostrarlos en una lista, incluyendo la próxima dosis.

TreatmentActivity.java: La pantalla donde se registran nuevos tratamientos con sus detalles.

🏃‍♀️ Guía de Ejecución
¡Sigue estos pasos para poner MiAbue a funcionar en tu máquina!

Requisitos Previos
Android Studio: Asegúrate de tener la última versión instalada.

JDK (Java Development Kit): Versión 11 o superior recomendada (normalmente viene con Android Studio).

Dispositivo Android o Emulador: Para probar la aplicación.

Pasos
Clona el Repositorio:
Abre tu terminal y ejecuta:

Bash

git clone https://github.com/tu-usuario/MiAbue.git
cd MiAbue
(No olvides reemplazar tu-usuario/MiAbue.git con la URL real de tu repositorio)

Abre el Proyecto en Android Studio:

Inicia Android Studio.

Ve a File > Open y selecciona la carpeta MiAbue que acabas de clonar.

Sincroniza Gradle:

Android Studio debería sincronizar Gradle automáticamente. Si no, haz clic en el icono del elefante con la flecha verde (🐘🔄) en la barra de herramientas.

Configura un Emulador o Conecta un Dispositivo:

Emulador: Tools > Device Manager > Create device y sigue los pasos.

Dispositivo Físico: Habilita "Opciones de desarrollador" y "Depuración USB" en tu teléfono, luego conéctalo.

¡Ejecuta la Aplicación!

Selecciona tu emulador o dispositivo desde el menú desplegable en Android Studio.

Haz clic en el botón Run 'app' (el icono del triángulo verde ▶️) en la barra de herramientas.
