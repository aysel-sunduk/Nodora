// Not: URL değişiminde logout işlemi genellikle burada yapılmaz, ancak form doğrulama sırasında yetkisiz erişim tespit edilirse
// üst seviye bir logout fonksiyonu çağrılabilir.

export const validateEmail = (email) =>
  /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

export const validatePassword = (password) =>
  password.length >= 8;
