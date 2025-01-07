export async function fetchWithAuth(url, options = {}) {
  const token = localStorage.getItem("jwt");
  const headers = { ...options.headers };

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  return fetch(url, {
    ...options,
    headers,
  });
}
