export type Category = "Tecnología" | "Ciencia" | "Mundo" | "Cultura" | "Ideas";
export type CatalogItem = {
  id: string;
  title: string;
  category: Category;
  description: string;
  body: string[];
  tags: string[];
  minutes: number;
  accent: string;
  icon: string;
  kicker: string;
};

export const categories: Category[] = ["Tecnología", "Ciencia", "Mundo", "Cultura", "Ideas"];

/**
 * Contenido editorial ilustrativo, no noticias verificadas en tiempo real.
 * Evita atribuciones falsas y fechas de publicación inventadas.
 */
export const catalog: CatalogItem[] = [
  {
    id: "1", category: "Tecnología", kicker: "FUTURO CERCANO",
    title: "La ciudad que aprende de sus habitantes",
    description: "Sensores, movilidad y datos: cómo imaginar espacios urbanos que se adapten a las personas.",
    body: [
      "Las ciudades inteligentes son mucho más que pantallas y semáforos conectados. El reto es utilizar la tecnología para mejorar la vida cotidiana y tomar decisiones públicas informadas.",
      "Los sistemas de transporte, el consumo energético y la gestión del agua pueden analizarse de forma conjunta. Para hacerlo bien, también hacen falta normas claras de privacidad y participación ciudadana.",
      "Esta lectura es un ejemplo editorial creado para la demostración de Buscador; no describe una noticia publicada hoy."
    ],
    tags: ["ciudades", "innovación", "urbanismo"], minutes: 4, accent: "#F35369", icon: "⌘"
  },
  {
    id: "2", category: "Ciencia", kicker: "NUEVAS PREGUNTAS",
    title: "Lo que todavía no sabemos del océano",
    description: "Una mirada a los misterios que siguen escondidos bajo la superficie.",
    body: [
      "La exploración de las profundidades marinas sigue planteando preguntas fascinantes sobre ecosistemas, especies y procesos geológicos.",
      "Los vehículos autónomos, los sensores y la observación científica ayudan a estudiar regiones donde es difícil mantener una presencia humana.",
      "Este artículo es material de demostración y no representa un reporte científico actualizado."
    ],
    tags: ["océano", "exploración", "ciencia"], minutes: 3, accent: "#50BCD4", icon: "≈"
  },
  {
    id: "3", category: "Ideas", kicker: "CAMBIAR LA MIRADA",
    title: "Aprender en la era de la inteligencia artificial",
    description: "Qué habilidades ganan valor cuando las herramientas responden casi cualquier pregunta.",
    body: [
      "Aprender ya no consiste únicamente en recordar información. Formular buenas preguntas, comparar fuentes y comprobar resultados se vuelve especialmente importante.",
      "Una herramienta puede sugerir respuestas, pero la interpretación del contexto y la responsabilidad de cada decisión continúan siendo humanas.",
      "Contenido de ejemplo para visualizar el formato de lectura de Buscador."
    ],
    tags: ["educación", "IA", "aprendizaje"], minutes: 5, accent: "#B99DF3", icon: "✳"
  },
  {
    id: "4", category: "Mundo", kicker: "CAMBIO URBANO",
    title: "Repensar la movilidad de nuestras ciudades",
    description: "Calles más caminables, transporte integrado y nuevas maneras de conectar barrios.",
    body: [
      "La movilidad urbana puede entenderse como una red de decisiones que afecta al tiempo, al ambiente y a la calidad de vida.",
      "Diseñar desplazamientos seguros implica escuchar a peatones, ciclistas, conductores y usuarios del transporte público.",
      "Esta historia es un ejemplo educativo, no una noticia de última hora."
    ],
    tags: ["transporte", "ciudad", "sostenibilidad"], minutes: 4, accent: "#EAAA70", icon: "↗"
  },
  {
    id: "5", category: "Cultura", kicker: "CREATIVIDAD DIGITAL",
    title: "Cuando el arte se encuentra con el código",
    description: "Diseño generativo, experiencias interactivas y nuevas formas de crear.",
    body: [
      "Los artistas y desarrolladores pueden crear juntos experiencias que combinan movimiento, sonido, visualización y participación del público.",
      "La tecnología no sustituye la intención creativa: abre nuevas posibilidades para imaginar y experimentar.",
      "Lectura ficticia de demostración para el diseño de esta aplicación."
    ],
    tags: ["arte", "diseño", "creatividad"], minutes: 3, accent: "#F28FB0", icon: "✦"
  },
  {
    id: "6", category: "Tecnología", kicker: "IDEAS QUE IMPORTAN",
    title: "Baterías: una pieza del futuro energético",
    description: "Por qué almacenar energía es tan importante como producirla.",
    body: [
      "Las tecnologías de almacenamiento ayudan a utilizar la energía cuando se necesita, incluso si su producción no ocurre en ese momento.",
      "Rendimiento, seguridad, materiales y reciclaje son aspectos que influyen en el desarrollo de mejores baterías.",
      "Este contenido introductorio es ilustrativo y no anuncia un descubrimiento reciente."
    ],
    tags: ["energía", "tecnología", "materiales"], minutes: 4, accent: "#90CDA9", icon: "ϟ"
  },
  {
    id: "7", category: "Ciencia", kicker: "NUEVA PERSPECTIVA",
    title: "Qué pueden enseñarnos los mapas de datos",
    description: "La visualización transforma cifras complejas en preguntas comprensibles.",
    body: [
      "Los datos adquieren significado cuando conocemos de dónde vienen, qué representan y cuáles son sus límites.",
      "Una visualización bien construida puede revelar patrones, pero debe evitar escalas engañosas o comparaciones fuera de contexto.",
      "Contenido ilustrativo, no basado en estadísticas de actualidad."
    ],
    tags: ["datos", "investigación", "mapas"], minutes: 3, accent: "#87B8F8", icon: "◈"
  },
  {
    id: "8", category: "Ideas", kicker: "EL PRÓXIMO PASO",
    title: "Pequeños cambios, grandes experiencias",
    description: "Cómo el diseño centrado en las personas transforma productos cotidianos.",
    body: [
      "Un buen producto no necesita mostrar todas sus capacidades a la vez. Debe ayudar a completar tareas con claridad y confianza.",
      "En diseño digital, la accesibilidad, la velocidad y el control del usuario resultan tan importantes como la apariencia.",
      "Esta lectura pertenece al catálogo de demostración de Buscador."
    ],
    tags: ["producto", "experiencia", "diseño"], minutes: 3, accent: "#EF8E6E", icon: "◎"
  }
];

export const briefIds = ["1", "3", "2"];
export const radarSignals = [
  { title: "Inteligencia artificial", category: "Tecnología" as Category, score: 84, tint: "#F35369", articleId: "3" },
  { title: "Vida en las ciudades", category: "Mundo" as Category, score: 69, tint: "#EAAA70", articleId: "4" },
  { title: "Exploración científica", category: "Ciencia" as Category, score: 58, tint: "#50BCD4", articleId: "2" },
  { title: "Diseño y creatividad", category: "Cultura" as Category, score: 42, tint: "#B99DF3", articleId: "5" }
];
