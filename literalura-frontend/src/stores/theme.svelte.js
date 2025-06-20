import { browser } from '$app/environment';

/**
 * Store reactivo para manejar el tema (dark/light)
 * Persiste la preferencia en localStorage
 */
export class ThemeStore {
	mode = $state('light');

	constructor() {
		if (browser) {
			// Leer preferencia guardada o usar preferencia del sistema
			const saved = localStorage.getItem('theme');
			const systemPrefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
			
			this.mode = saved || (systemPrefersDark ? 'dark' : 'light');
			this.applyTheme();

			// Escuchar cambios en preferencia del sistema
			window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
				if (!localStorage.getItem('theme')) {
					this.mode = e.matches ? 'dark' : 'light';
					this.applyTheme();
				}
			});
		}
	}

	toggle() {
		this.mode = this.mode === 'light' ? 'dark' : 'light';
		this.applyTheme();
		this.persist();
	}

	setMode(mode) {
		this.mode = mode;
		this.applyTheme();
		this.persist();
	}

	applyTheme() {
		if (browser) {
			document.documentElement.classList.toggle('dark', this.mode === 'dark');
		}
	}

	persist() {
		if (browser) {
			localStorage.setItem('theme', this.mode);
		}
	}

	get isDark() {
		return this.mode === 'dark';
	}

	get isLight() {
		return this.mode === 'light';
	}
}

export const themeStore = new ThemeStore(); 