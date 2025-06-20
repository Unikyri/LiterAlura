// En desarrollo, Vite proxy maneja /api -> http://localhost:8080/api
// En producción, se puede configurar el mismo proxy o usar la URL completa
const API_BASE_URL = '/api';

/**
 * Store reactivo para manejar llamadas a la API del backend
 * Usa Svelte 5 runes para reactividad
 */
export class ApiStore {
	loading = $state(false);
	error = $state(null);

	constructor() {
		this.loading = false;
		this.error = null;
	}

	async request(endpoint, options = {}) {
		this.loading = true;
		this.error = null;

		try {
			const response = await fetch(`${API_BASE_URL}${endpoint}`, {
				headers: {
					'Content-Type': 'application/json',
					...options.headers
				},
				...options
			});

			if (!response.ok) {
				const errorData = await response.json().catch(() => ({}));
				throw new Error(errorData.error || `HTTP ${response.status}: ${response.statusText}`);
			}

			const data = await response.json();
			return data;
		} catch (err) {
			this.error = err.message;
			throw err;
		} finally {
			this.loading = false;
		}
	}

	// Métodos específicos para cada endpoint
	async searchBook(title) {
		return this.request(`/books/search?title=${encodeURIComponent(title)}`);
	}

	async getAllBooks() {
		return this.request('/books');
	}

	async getBooksByLanguage(language) {
		return this.request(`/books/language?lang=${encodeURIComponent(language)}`);
	}

	async getTopBooks() {
		return this.request('/books/top');
	}

	async getAllAuthors() {
		return this.request('/authors');
	}

	async getAuthorsAliveInYear(year) {
		return this.request(`/authors/alive?year=${year}`);
	}

	clearError() {
		this.error = null;
	}
}

// Instancia singleton
export const apiStore = new ApiStore(); 