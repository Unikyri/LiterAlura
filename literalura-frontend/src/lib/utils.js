import { clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

/**
 * Combina clases de Tailwind CSS de manera inteligente
 */
export function cn(...inputs) {
	return twMerge(clsx(inputs));
}

/**
 * Formatea números para display
 */
export function formatNumber(num) {
	if (num >= 1000000) {
		return (num / 1000000).toFixed(1) + 'M';
	}
	if (num >= 1000) {
		return (num / 1000).toFixed(1) + 'K';
	}
	return num.toString();
}

/**
 * Trunca texto a longitud específica
 */
export function truncateText(text, maxLength = 100) {
	if (!text) return '';
	return text.length > maxLength ? text.slice(0, maxLength) + '...' : text;
}

/**
 * Debounce function para optimizar búsquedas
 */
export function debounce(func, wait) {
	let timeout;
	return function executedFunction(...args) {
		const later = () => {
			clearTimeout(timeout);
			func(...args);
		};
		clearTimeout(timeout);
		timeout = setTimeout(later, wait);
	};
}

/**
 * Valida si una URL de imagen es válida
 */
export function isValidImageUrl(url) {
	if (!url) return false;
	return /\.(jpg|jpeg|png|webp|gif)$/i.test(url);
}

/**
 * Genera un color aleatorio para avatares
 */
export function generateAvatarColor(seed) {
	const colors = [
		'bg-red-500', 'bg-blue-500', 'bg-green-500', 'bg-yellow-500',
		'bg-purple-500', 'bg-pink-500', 'bg-indigo-500', 'bg-teal-500'
	];
	const index = seed ? seed.charCodeAt(0) % colors.length : Math.floor(Math.random() * colors.length);
	return colors[index];
}

/**
 * Formatea fechas de manera legible
 */
export function formatDate(date, locale = 'es-ES') {
	if (!date) return '';
	const d = new Date(date);
	return d.toLocaleDateString(locale, {
		year: 'numeric',
		month: 'long',
		day: 'numeric'
	});
}

/**
 * Convierte año a década
 */
export function getDecade(year) {
	return Math.floor(year / 10) * 10;
}

/**
 * Obtiene iniciales de un nombre
 */
export function getInitials(name) {
	if (!name) return 'NA';
	return name
		.split(' ')
		.map(word => word.charAt(0))
		.join('')
		.toUpperCase()
		.slice(0, 2);
}

/**
 * Scroll suave a un elemento
 */
export function scrollToElement(elementId, offset = 0) {
	const element = document.getElementById(elementId);
	if (element) {
		const top = element.offsetTop - offset;
		window.scrollTo({
			top,
			behavior: 'smooth'
		});
	}
}

/**
 * Copia texto al clipboard
 */
export async function copyToClipboard(text) {
	try {
		await navigator.clipboard.writeText(text);
		return true;
	} catch (err) {
		console.error('Failed to copy text: ', err);
		return false;
	}
}

/**
 * Detecta si el dispositivo es móvil
 */
export function isMobile() {
	return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(
		navigator.userAgent
	);
}

/**
 * Genera slug amigable para URLs
 */
export function slugify(text) {
	return text
		.toString()
		.toLowerCase()
		.normalize('NFD')
		.replace(/[\u0300-\u036f]/g, '')
		.replace(/\s+/g, '-')
		.replace(/[^\w-]+/g, '')
		.replace(/--+/g, '-')
		.replace(/^-+/, '')
		.replace(/-+$/, '');
}

/**
 * Calcula tiempo de lectura estimado
 */
export function calculateReadingTime(text, wordsPerMinute = 200) {
	if (!text) return 0;
	const words = text.split(/\s+/).length;
	return Math.ceil(words / wordsPerMinute);
}

/**
 * Convierte hex a RGB
 */
export function hexToRgb(hex) {
	const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
	return result ? {
		r: parseInt(result[1], 16),
		g: parseInt(result[2], 16),
		b: parseInt(result[3], 16)
	} : null;
}

/**
 * Genera gradiente aleatorio
 */
export function generateGradient() {
	const gradients = [
		'from-blue-500 to-cyan-500',
		'from-purple-500 to-pink-500',
		'from-green-500 to-emerald-500',
		'from-orange-500 to-red-500',
		'from-indigo-500 to-purple-500',
		'from-rose-500 to-pink-500',
		'from-teal-500 to-cyan-500',
		'from-amber-500 to-orange-500'
	];
	return gradients[Math.floor(Math.random() * gradients.length)];
} 