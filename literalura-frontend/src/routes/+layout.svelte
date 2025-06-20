<script>
	import '../app.css';
	import { themeStore } from '$stores/theme.svelte.js';
	import { page } from '$app/stores';
	import Icon from '../components/Icon.svelte';
	import { fly, scale } from 'svelte/transition';
	import { quintOut } from 'svelte/easing';

	let mobileMenuOpen = $state(false);

	// Background dinámico basado en la ruta
	let backgroundClass = $derived(() => {
		const pathname = $page.url.pathname;
		if (pathname === '/') return 'bg-gradient-to-br from-blue-50 via-purple-50 to-indigo-50 dark:from-gray-900 dark:via-blue-950 dark:to-purple-950';
		if (pathname.startsWith('/search')) return 'bg-gradient-to-br from-green-50 via-blue-50 to-teal-50 dark:from-gray-900 dark:via-green-950 dark:to-blue-950';
		if (pathname.startsWith('/library')) return 'bg-gradient-to-br from-purple-50 via-pink-50 to-rose-50 dark:from-gray-900 dark:via-purple-950 dark:to-pink-950';
		if (pathname.startsWith('/stats')) return 'bg-gradient-to-br from-orange-50 via-amber-50 to-yellow-50 dark:from-gray-900 dark:via-orange-950 dark:to-amber-950';
		return 'bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-900 dark:to-gray-800';
	});

	const navLinks = [
		{ href: '/', label: 'Inicio' },
		{ href: '/search', label: 'Buscar' },
		{ href: '/library', label: 'Biblioteca' },
		{ href: '/stats', label: 'Estadísticas' }
	];

	// Floating particles animation
	let particles = Array.from({ length: 20 }, (_, i) => ({
		id: i,
		x: Math.random() * 100,
		y: Math.random() * 100,
		size: Math.random() * 3 + 1,
		duration: Math.random() * 20 + 10
	}));
</script>

<svelte:head>
	<title>LiterAlura - Catálogo de Libros</title>
</svelte:head>

<div class="min-h-screen {backgroundClass} transition-all duration-1000 relative overflow-hidden">
	<!-- Animated Background Particles -->
	<div class="absolute inset-0 pointer-events-none">
		{#each particles as particle (particle.id)}
			<div
				class="absolute w-{particle.size} h-{particle.size} bg-white/20 dark:bg-white/10 rounded-full"
				style="
					left: {particle.x}%;
					top: {particle.y}%;
					animation: float {particle.duration}s ease-in-out infinite;
					animation-delay: {particle.id * 0.5}s;
				"
			></div>
		{/each}
	</div>

	<!-- Navigation -->
	<nav class="glass border-b border-white/20 backdrop-blur-xl sticky top-0 z-50">
		<div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
			<div class="flex justify-between items-center h-16">
				<!-- Logo -->
				<a href="/" class="flex items-center space-x-3 group">
					<Icon name="BookOpen" class="w-8 h-8 text-brand-600 dark:text-brand-400 group-hover:scale-110 transition-transform" />
					<div class="hidden sm:block">
						<h1 class="text-xl font-bold gradient-text dark:gradient-text-dark">LiterAlura</h1>
						<p class="text-xs text-muted-foreground -mt-1">Catálogo Interactivo</p>
					</div>
				</a>

				<!-- Desktop Navigation -->
				<div class="hidden md:flex items-center space-x-1">
					{#each navLinks as link}
						<a
							href={link.href}
							class="px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200
								hover:bg-white/20 dark:hover:bg-white/10 hover:scale-105
								{$page.url.pathname === link.href ? 'bg-white/30 dark:bg-white/20 text-brand-700 dark:text-brand-300' : 'text-foreground/70 hover:text-foreground'}"
						>
							{link.label}
						</a>
					{/each}
				</div>

				<!-- Mobile menu button & Theme toggle -->
				<div class="flex items-center space-x-2">
					<!-- Theme Toggle -->
					<button
						onclick={() => themeStore.toggle()}
						class="p-2 rounded-lg bg-white/20 dark:bg-white/10 backdrop-blur-sm
							hover:bg-white/30 dark:hover:bg-white/20 transition-all duration-200
							hover:scale-110"
						aria-label="Cambiar tema"
					>
						{#if themeStore.isDark}
							<Icon name="Sun" class="w-5 h-5 text-amber-500" />
						{:else}
							<Icon name="Moon" class="w-5 h-5 text-indigo-600" />
						{/if}
					</button>

					<!-- Mobile menu button -->
					<button
						onclick={() => mobileMenuOpen = !mobileMenuOpen}
						class="md:hidden p-2 rounded-lg bg-white/20 dark:bg-white/10"
						aria-label="Menú móvil"
					>
						{#if mobileMenuOpen}
							<Icon name="X" class="w-5 h-5" />
						{:else}
							<Icon name="Menu" class="w-5 h-5" />
						{/if}
					</button>
				</div>
			</div>

			<!-- Mobile Navigation -->
			{#if mobileMenuOpen}
				<div class="md:hidden py-4 border-t border-white/20" transition:fly={{ y: -10, duration: 200 }}>
					{#each navLinks as link}
						<a
							href={link.href}
							onclick={() => mobileMenuOpen = false}
							class="block px-4 py-2 text-sm font-medium rounded-lg transition-colors
								{$page.url.pathname === link.href ? 'bg-white/30 dark:bg-white/20 text-brand-700 dark:text-brand-300' : 'text-foreground/70 hover:text-foreground hover:bg-white/10'}"
						>
							{link.label}
						</a>
					{/each}
				</div>
			{/if}
		</div>
	</nav>

	<!-- Main Content -->
	<main class="relative z-10">
		{#key $page.url.pathname}
			<div transition:fly={{ y: 20, duration: 400, easing: quintOut }}>
				<slot />
			</div>
		{/key}
	</main>

	<!-- Footer -->
	<footer class="relative z-10 mt-auto border-t border-white/20 glass backdrop-blur-xl">
		<div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
			<div class="grid grid-cols-1 md:grid-cols-3 gap-8">
				<!-- About -->
				<div class="space-y-4">
					<h3 class="font-semibold text-foreground">Sobre LiterAlura</h3>
					<p class="text-sm text-muted-foreground">
						Plataforma moderna para explorar y gestionar libros usando la API de OpenLibrary.
						Construido con ❤️ usando Spring Boot y Svelte.
					</p>
				</div>

				<!-- Features -->
				<div class="space-y-4">
					<h3 class="font-semibold text-foreground">Características</h3>
					<ul class="text-sm text-muted-foreground space-y-2">
						<li>• Búsqueda en tiempo real</li>
						<li>• Filtros avanzados</li>
						<li>• Estadísticas detalladas</li>
						<li>• Interfaz moderna y responsiva</li>
					</ul>
				</div>

				<!-- Tech Stack -->
				<div class="space-y-4">
					<h3 class="font-semibold text-foreground">Tecnologías</h3>
					<div class="flex flex-wrap gap-2">
						{#each ['Svelte 5', 'SvelteKit', 'Spring Boot', 'PostgreSQL', 'Tailwind CSS'] as tech}
							<span class="px-3 py-1 text-xs bg-white/20 dark:bg-white/10 rounded-full border border-white/20">
								{tech}
							</span>
						{/each}
					</div>
				</div>
			</div>

			<div class="mt-8 pt-8 border-t border-white/20 text-center">
				<p class="text-sm text-muted-foreground">
					© 2025 LiterAlura. Proyecto educativo de Alura Challenges.
				</p>
			</div>
		</div>
	</footer>
</div>

<style>
	:global(.glass) {
		backdrop-filter: blur(16px);
		-webkit-backdrop-filter: blur(16px);
	}
</style> 