export type Category = "Tecnología" | "Educación" | "Exploración" | "Productividad";
export type CatalogItem = {
  id: string;
  title: string;
  category: Category;
  description: string;
  tags: string[];
};

export const categories: Category[] = ["Tecnología", "Educación", "Exploración", "Productividad"];
export const catalog: CatalogItem[] = [
  { id: "1", title: "Guía de programación", category: "Educación", description: "Recursos básicos para comenzar con TypeScript y React Native.", tags: ["código", "aprendizaje"] },
  { id: "2", title: "Ideas de diseño móvil", category: "Tecnología", description: "Inspiración para crear interfaces limpias y modernas.", tags: ["interfaz", "diseño"] },
  { id: "3", title: "Organizar mis proyectos", category: "Productividad", description: "Una forma sencilla de ordenar tareas y objetivos.", tags: ["tareas", "organización"] },
  { id: "4", title: "Descubrir nuevas herramientas", category: "Exploración", description: "Explora herramientas útiles para estudiar y trabajar.", tags: ["descubrir", "herramientas"] },
  { id: "5", title: "Conceptos de bases de datos", category: "Educación", description: "Introducción a MongoDB y su modelo de documentos.", tags: ["mongodb", "datos"] },
  { id: "6", title: "Buenas prácticas de seguridad", category: "Tecnología", description: "Consejos para proteger información y aplicaciones.", tags: ["privacidad", "seguridad"] }
];
