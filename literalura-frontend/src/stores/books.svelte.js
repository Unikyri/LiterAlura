import { apiStore } from './api.svelte.js';

/**
 * Store reactivo para manejar el estado de libros
 * Implementa cache inteligente y optimistic updates
 */
export class BooksStore {
	books = $state([]);
	topBooks = $state([]);
	searchResults = $state([]);
	currentBook = $state(null);
	filters = $state({
		language: '',
		searchTerm: '',
		sortBy: 'searchCount',
		sortOrder: 'desc'
	});
	
	// Cache management
	cache = $state({
		books: { data: [], timestamp: 0, ttl: 5 * 60 * 1000 }, // 5 min TTL
		topBooks: { data: [], timestamp: 0, ttl: 2 * 60 * 1000 }, // 2 min TTL
		languages: new Map() // cache por idioma
	});

	constructor() {
		this.books = [];
		this.topBooks = [];
		this.searchResults = [];
		this.currentBook = null;
	}

	// Verificar si cache es válido
	isCacheValid(cacheKey) {
		const cached = this.cache[cacheKey];
		return cached && (Date.now() - cached.timestamp) < cached.ttl;
	}

	async loadAllBooks(forceRefresh = false) {
		if (!forceRefresh && this.isCacheValid('books')) {
			this.books = this.cache.books.data;
			return this.books;
		}

		try {
			const data = await apiStore.getAllBooks();
			this.books = data;
			
			// Actualizar cache
			this.cache.books = {
				data: data,
				timestamp: Date.now(),
				ttl: 5 * 60 * 1000
			};
			
			return data;
		} catch (error) {
			console.error('Error loading books:', error);
			return [];
		}
	}

	async loadTopBooks(forceRefresh = false) {
		if (!forceRefresh && this.isCacheValid('topBooks')) {
			this.topBooks = this.cache.topBooks.data;
			return this.topBooks;
		}

		try {
			const data = await apiStore.getTopBooks();
			this.topBooks = data;
			
			// Actualizar cache
			this.cache.topBooks = {
				data: data,
				timestamp: Date.now(),
				ttl: 2 * 60 * 1000
			};
			
			return data;
		} catch (error) {
			console.error('Error loading top books:', error);
			return [];
		}
	}

	async searchBook(title) {
		if (!title?.trim()) {
			this.searchResults = [];
			return [];
		}

		try {
			const book = await apiStore.searchBook(title);
			
			// Optimistic update - agregar a la lista si es nuevo
			const existingIndex = this.books.findIndex(b => b.id === book.id);
			if (existingIndex === -1) {
				this.books = [book, ...this.books];
			} else {
				// Actualizar libro existente
				this.books[existingIndex] = book;
			}
			
			this.searchResults = [book];
			this.currentBook = book;
			
			// Invalidar caches relevantes
			this.invalidateCache(['books', 'topBooks']);
			
			return book;
		} catch (error) {
			console.error('Error searching book:', error);
			this.searchResults = [];
			throw error;
		}
	}

	async loadBooksByLanguage(language) {
		// Verificar cache por idioma
		const cacheKey = `lang_${language}`;
		const cached = this.cache.languages.get(cacheKey);
		
		if (cached && (Date.now() - cached.timestamp) < 3 * 60 * 1000) {
			return cached.data;
		}

		try {
			const data = await apiStore.getBooksByLanguage(language);
			
			// Actualizar cache de idioma
			this.cache.languages.set(cacheKey, {
				data: data,
				timestamp: Date.now()
			});
			
			return data;
		} catch (error) {
			console.error('Error loading books by language:', error);
			return [];
		}
	}

	// Filtros y ordenamiento local
	get filteredBooks() {
		let filtered = [...this.books];

		// Filtrar por idioma
		if (this.filters.language) {
			filtered = filtered.filter(book => 
				book.language?.toLowerCase() === this.filters.language.toLowerCase()
			);
		}

		// Filtrar por término de búsqueda
		if (this.filters.searchTerm) {
			const term = this.filters.searchTerm.toLowerCase();
			filtered = filtered.filter(book =>
				book.title?.toLowerCase().includes(term) ||
				book.author?.name?.toLowerCase().includes(term)
			);
		}

		// Ordenar
		filtered.sort((a, b) => {
			const aVal = a[this.filters.sortBy] || 0;
			const bVal = b[this.filters.sortBy] || 0;
			
			if (this.filters.sortOrder === 'desc') {
				return bVal > aVal ? 1 : -1;
			} else {
				return aVal > bVal ? 1 : -1;
			}
		});

		return filtered;
	}

	// Estadísticas derivadas
	get statistics() {
		const total = this.books.length;
		const languages = [...new Set(this.books.map(b => b.language).filter(Boolean))];
		const authors = [...new Set(this.books.map(b => b.author?.name).filter(Boolean))];
		const totalSearches = this.books.reduce((sum, book) => sum + (book.searchCount || 0), 0);

		return {
			totalBooks: total,
			totalLanguages: languages.length,
			totalAuthors: authors.length,
			totalSearches,
			languages,
			authors: authors.slice(0, 10) // Top 10 autores
		};
	}

	// Utilidades
	setFilter(key, value) {
		this.filters[key] = value;
	}

	clearFilters() {
		this.filters = {
			language: '',
			searchTerm: '',
			sortBy: 'searchCount',
			sortOrder: 'desc'
		};
	}

	invalidateCache(keys = []) {
		keys.forEach(key => {
			if (this.cache[key]) {
				this.cache[key].timestamp = 0;
			}
		});
		
		// Limpiar cache de idiomas si se especifica
		if (keys.includes('languages')) {
			this.cache.languages.clear();
		}
	}

	selectBook(book) {
		this.currentBook = book;
	}

	clearSelection() {
		this.currentBook = null;
	}
}

export const booksStore = new BooksStore(); 