// Not: Rol bazlı erişim kontrolü yaparken, kullanıcı yetkisiz bir sayfaya erişirse
// logout fonksiyonu çağrılabilir. Bu kontrolü genellikle route veya context seviyesinde yapmak daha uygundur.

export const hasRole = (user, role) => user?.role === role;

export const canEditBoard = (user) => ["admin", "editor"].includes(user?.role);

// CSS sınıflarını birleştirmek için yardımcı fonksiyon
export function cn(...classes) {
  return classes.filter(Boolean).join(" ");
}
