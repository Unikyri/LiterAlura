// Tipos principales de la aplicación

export interface Book {
	id: string;
	openLibraryId: string;
	title: string;
	firstPublishYear?: number;
	coverId?: string;
	language?: string;
	searchCount: number;
	author?: Author;
}

export interface Author {
	id: string;
	openLibraryId: string;
	name: string;
	birthDate?: string;
	deathDate?: string;
	bio?: string;
	books?: Book[];
}

export interface ApiResponse<T> {
	data: T;
	success: boolean;
	message?: string;
	error?: string;
}

export interface SearchResult {
	book: Book;
	relevance: number;
}

export interface FilterOptions {
	language?: string;
	searchTerm?: string;
	sortBy?: SortOption;
	sortOrder?: 'asc' | 'desc';
	yearFrom?: number;
	yearTo?: number;
}

export type SortOption = 'title' | 'author' | 'year' | 'searchCount' | 'relevance';

export interface Statistics {
	totalBooks: number;
	totalAuthors: number;
	totalLanguages: number;
	totalSearches: number;
	languages: string[];
	authors: string[];
}

export interface CacheEntry<T> {
	data: T;
	timestamp: number;
	ttl: number;
}

export interface Theme {
	mode: 'light' | 'dark';
	primary: string;
	secondary: string;
	accent: string;
}

export interface ToastMessage {
	id: string;
	type: 'success' | 'error' | 'warning' | 'info';
	title: string;
	message?: string;
	duration?: number;
	actions?: ToastAction[];
}

export interface ToastAction {
	label: string;
	action: () => void;
	style?: 'primary' | 'secondary';
}

export interface NavigationItem {
	href: string;
	label: string;
	icon?: string;
	badge?: string | number;
	children?: NavigationItem[];
}

export interface ComponentProps {
	class?: string;
	style?: string;
	id?: string;
}

export interface LoadingState {
	isLoading: boolean;
	message?: string;
	progress?: number;
}

export interface ErrorState {
	hasError: boolean;
	message?: string;
	code?: string | number;
	details?: any;
}

// Event types para componentes
export interface BookCardEvents {
	click: CustomEvent<Book>;
	like: CustomEvent<{ book: Book; liked: boolean }>;
	share: CustomEvent<Book>;
}

export interface SearchEvents {
	search: CustomEvent<string>;
	filter: CustomEvent<FilterOptions>;
	clear: CustomEvent<void>;
}

// Utility types
export type DeepPartial<T> = {
	[P in keyof T]?: T[P] extends object ? DeepPartial<T[P]> : T[P];
};

export type RequiredFields<T, K extends keyof T> = T & Required<Pick<T, K>>;

export type OptionalFields<T, K extends keyof T> = Omit<T, K> & Partial<Pick<T, K>>;

// Configuración de la aplicación
export interface AppConfig {
	apiBaseUrl: string;
	defaultLanguage: string;
	itemsPerPage: number;
	cacheTimeout: number;
	features: {
		darkMode: boolean;
		analytics: boolean;
		sharing: boolean;
		offline: boolean;
	};
} 