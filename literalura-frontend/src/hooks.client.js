/**
 * Hooks del cliente para SvelteKit
 * Maneja errores y configuraciones específicas del navegador
 */

import { dev } from '$app/environment';

// Prevenir errores de favicon en desarrollo
if (dev) {
	// Interceptar errores 404 de favicon y silenciarlos
	const originalError = console.error;
	console.error = (...args) => {
		const message = args.join(' ');
		// Silenciar errores conocidos de favicon en desarrollo
		if (message.includes('favicon') || message.includes('404')) {
			return;
		}
		originalError.apply(console, args);
	};
}

// Manejar errores no capturados
export const handleError = ({ error, event }) => {
	console.error('Error no capturado:', error);
	
	return {
		message: dev ? error.message : 'Ha ocurrido un error interno',
		code: error.code ?? 'UNKNOWN'
	};
}; 