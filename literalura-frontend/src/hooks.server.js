/**
 * Hooks del servidor para SvelteKit
 * Maneja errores del servidor y configuraciones de rutas
 */

import { dev } from '$app/environment';

// Manejar errores del servidor
export const handleError = ({ error, event }) => {
	// Log completo en desarrollo
	if (dev) {
		console.error('Error del servidor:', {
			message: error.message,
			stack: error.stack,
			url: event.url.pathname
		});
	}
	
	return {
		message: dev ? error.message : 'Error interno del servidor',
		code: error.code ?? 'INTERNAL_ERROR'
	};
};

// Manejar requests
export const handle = async ({ event, resolve }) => {
	// Manejar favicons directamente sin errores
	if (event.url.pathname.includes('favicon')) {
		return new Response(null, { status: 204 });
	}

	// Continuar con el manejo normal
	const response = await resolve(event);
	
	// Agregar headers de seguridad y cache control para HTML
	response.headers.set('X-Content-Type-Options', 'nosniff');
	response.headers.set('X-Frame-Options', 'DENY');
	response.headers.set('X-XSS-Protection', '1; mode=block');
	
	// Prevenir cache de HTML para evitar bucles de recarga
	if (response.headers.get('content-type')?.includes('text/html')) {
		response.headers.set('Cache-Control', 'no-cache, no-store, must-revalidate');
		response.headers.set('Pragma', 'no-cache');
		response.headers.set('Expires', '0');
	}
	
	return response;
}; 