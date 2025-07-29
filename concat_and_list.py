#!/usr/bin/env python3
import os
import csv
from collections import defaultdict

# ------------------------------------------------------------------
# 1) CONFIGURACIÓN DE EXTENSIONES Y NOMBRES CLAVE
# ------------------------------------------------------------------

# Extensiones a incluir en la concatenación
INCLUDE_EXT = {
    # Lenguajes de programación
    ".java", ".py", ".dart", ".kt", ".scala", ".js", ".ts", ".jsx", ".tsx",
    ".c", ".cpp", ".cs", ".go", ".rb", ".php", ".swift", ".m", ".mm",
    ".pl", ".sh", ".bash", ".ps1", ".bat", ".cmd", ".groovy", ".r",
    # Configuración y recursos
    ".xml", ".jrxml", ".pom", ".gradle", ".fxml", ".properties", ".env",
    ".json", ".yml", ".yaml", ".toml", ".ini", ".conf", ".cfg",
    ".sql", ".html", ".css", ".scss", ".less", ".md", ".jsp", ".ftl",
    # Otros útiles
    ".log", ".csv", ".tsv", ".proto", ".graphql", ".gql", ".vue", ".svelte",
}

# Archivos sin extensión a incluir siempre
IMPORTANT_FILENAMES = {
    "Dockerfile", "Makefile", "Procfile",
    "README", "README.md", "LICENSE", "Pipfile", "requirements.txt"
}

# Directorios a excluir (control de versiones, builds, dependencias…)
EXCLUDE_DIRS = {
    ".git", ".hg", ".svn",
    "node_modules", "target", "build", "dist", "__pycache__",
    "venv", ".venv", "env", "bin", "obj"
}

# Extensiones de archivos binarios a excluir
EXCLUDE_FILES_EXT = {
    ".class", ".jar", ".exe", ".dll", ".so", ".dylib",
    ".pyc", ".pyo", ".o", ".obj", ".lib", ".a",
    ".war", ".ear", ".apk", ".ipa"
}

# ------------------------------------------------------------------
# 2) PREPARAR RUTAS Y NOMBRES DE SALIDA
# ------------------------------------------------------------------

ROOT = os.getcwd()
PROJECT_NAME = os.path.basename(ROOT.rstrip(os.sep))
OUT_TXT = f"{PROJECT_NAME}.txt"
OUT_CSV = f"{PROJECT_NAME}_estructura.csv"

# ------------------------------------------------------------------
# 3) RECORRER Y AGRUPAR
# ------------------------------------------------------------------

groups = defaultdict(list)
all_entries = []  # [(ruta_relativa, "File"/"Directory")]

for dirpath, dirnames, filenames in os.walk(ROOT):
    # Excluir carpetas irrelevantes
    dirnames[:] = [d for d in dirnames if d not in EXCLUDE_DIRS]
    rel_dir = os.path.relpath(dirpath, ROOT)
    all_entries.append((rel_dir or PROJECT_NAME, "Directory"))

    for fname in filenames:
        full = os.path.join(dirpath, fname)
        rel = os.path.relpath(full, ROOT)
        all_entries.append((rel, "File"))

        base, ext = os.path.splitext(fname)
        ext = ext.lower()

        # Decidir inclusión en la concatenación
        if (
            ext in INCLUDE_EXT
            or fname in IMPORTANT_FILENAMES
        ) and not any(fname.lower().endswith(x) for x in EXCLUDE_FILES_EXT):
            groups[rel_dir].append(full)

# ------------------------------------------------------------------
# 4) ESCRIBIR ARCHIVO TXT CONCATENADO
# ------------------------------------------------------------------

with open(OUT_TXT, "w", encoding="utf-8") as out:
    for folder in sorted(groups):
        header = folder if folder != "." else "(raíz)"
        out.write(f"--- {header}\n\n")
        for filepath in sorted(groups[folder]):
            name = os.path.basename(filepath)
            out.write(f"// >>> {name}\n")
            # Leer con fallback de codificación
            content = ""
            for enc in ("utf-8", "latin-1"):
                try:
                    with open(filepath, "r", encoding=enc) as src:
                        content = src.read()
                    break
                except UnicodeDecodeError:
                    continue
                except Exception as e:
                    content = f"[¡Error leyendo {name}: {e}]\n"
                    break
            out.write(content)
            out.write("\n\n")
print(f"✔ Contenido esencial concatenado en: {OUT_TXT}")

# ------------------------------------------------------------------
# 5) ESCRIBIR CSV DE ESTRUCTURA COMPLETA
# ------------------------------------------------------------------

with open(OUT_CSV, "w", newline="", encoding="utf-8") as csvf:
    writer = csv.writer(csvf)
    writer.writerow(["Path", "Type"])
    # Incluir la raíz
    writer.writerow([PROJECT_NAME, "Directory"])
    for rel, typ in sorted(all_entries):
        if rel == ".":
            continue
        writer.writerow([rel, typ])
print(f"✔ Estructura completa exportada en: {OUT_CSV}")
