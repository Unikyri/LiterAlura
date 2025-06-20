<script>
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import { booksStore } from '$stores/books.svelte.js';
	import { apiStore } from '$stores/api.svelte.js';
	import { themeStore } from '$stores/theme.svelte.js';
	import Icon from '../components/Icon.svelte';
	import { fly, scale, blur } from 'svelte/transition';
	import { quintOut, elasticOut } from 'svelte/easing';

	let searchTerm = $state('');
	let isSearching = $state(false);
	let heroStats = $state({
		totalBooks: 0,
		totalAuthors: 0,
		totalLanguages: 0,
		totalSearches: 0
	});

	// Animaciones escalonadas para las tarjetas
	let cardDelay = (index) => index * 100;

	// Efectos interactivos para el hero
	let mouseX = $state(0);
	let mouseY = $state(0);
	let heroRef;

	function handleMouseMove(event) {
		if (heroRef) {
			const rect = heroRef.getBoundingClientRect();
			mouseX = ((event.clientX - rect.left) / rect.width) * 100;
			mouseY = ((event.clientY - rect.top) / rect.height) * 100;
		}
	}

	// Búsqueda rápida desde el hero
	async function quickSearch(event) {
		event.preventDefault();
		if (!searchTerm.trim()) return;
		
		isSearching = true;
		try {
			await booksStore.searchBook(searchTerm);
			goto('/search?q=' + encodeURIComponent(searchTerm));
		} catch (error) {
			console.error('Error en búsqueda:', error);
		} finally {
			isSearching = false;
		}
	}

	// Cargar estadísticas al montar
	onMount(async () => {
		try {
			await booksStore.loadAllBooks();
			heroStats = booksStore.statistics;
		} catch (error) {
			console.error('Error cargando estadísticas:', error);
		}
	});

	// Datos para las tarjetas de funcionalidades
	const features = [
		{
			iconName: 'Search',
			title: 'Búsqueda Inteligente',
			description: 'Encuentra cualquier libro usando la potente API de OpenLibrary',
			gradient: 'from-blue-500 to-cyan-500',
			href: '/search'
		},
		{
			iconName: 'BookOpen',
			title: 'Biblioteca Personal',
			description: 'Gestiona y organiza todos tus libros favoritos',
			gradient: 'from-purple-500 to-pink-500',
			href: '/library'
		},
		{
			iconName: 'TrendingUp',
			title: 'Estadísticas',
			description: 'Descubre tendencias y patrones en tu colección',
			gradient: 'from-green-500 to-emerald-500',
			href: '/stats'
		},
		{
			iconName: 'Globe',
			title: 'Multidioma',
			description: 'Explora libros en diferentes idiomas del mundo',
			gradient: 'from-orange-500 to-red-500',
			href: '/library?filter=language'
		}
	];

	const quickActions = [
		{ label: 'Top Libros', action: () => goto('/stats#top-books') },
		{ label: 'Por Idioma', action: () => goto('/library?view=languages') },
		{ label: 'Autores', action: () => goto('/library?view=authors') },
		{ label: 'Recientes', action: () => goto('/library?sort=recent') }
	];
</script>

<svelte:head>
	<title>LiterAlura - Descubre el mundo de los libros</title>
	<meta name="description" content="Explora, busca y gestiona libros de forma inteligente con LiterAlura" />
</svelte:head>

<!-- Hero Section -->
<section 
	bind:this={heroRef}
	onmousemove={handleMouseMove}
	class="relative min-h-screen flex items-center justify-center overflow-hidden"
	role="banner"
>
	<!-- Interactive background -->
	<div 
		class="absolute inset-0 opacity-30"
		style="
			background: radial-gradient(circle at {mouseX}% {mouseY}%, 
				rgba(59, 130, 246, 0.3) 0%, 
				rgba(147, 51, 234, 0.2) 50%, 
				transparent 100%);
			transition: all 0.3s ease;
		"
	></div>

	<div class="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
		<!-- Main Hero Content -->
		<div class="space-y-8">
			<!-- Logo animado -->
			<div 
				class="inline-flex items-center justify-center w-24 h-24 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 shadow-2xl mb-8"
				transition:scale={{ duration: 800, easing: elasticOut }}
			>
				<Icon name="BookOpen" class="w-12 h-12 text-white animate-pulse" />
			</div>

			<!-- Título principal -->
			<div transition:fly={{ y: 50, duration: 800, delay: 200, easing: quintOut }}>
				<h1 class="text-6xl md:text-8xl font-bold mb-4">
					<span class="gradient-text dark:gradient-text-dark">Liter</span><span class="text-accent-500">Alura</span>
				</h1>
				<p class="text-xl md:text-2xl text-muted-foreground max-w-3xl mx-auto">
					Descubre, explora y gestiona el <span class="font-semibold text-foreground">universo de los libros</span> 
					con una experiencia completamente nueva
				</p>
			</div>

			<!-- Barra de búsqueda heroica -->
			<div 
				class="max-w-2xl mx-auto"
				transition:fly={{ y: 30, duration: 600, delay: 400, easing: quintOut }}
			>
				<form onsubmit={quickSearch} class="relative group">
					<div class="relative">
						<Icon name="Search" class="absolute left-4 top-1/2 transform -translate-y-1/2 w-6 h-6 text-muted-foreground group-focus-within:text-accent-500 transition-colors" />
						<input
							bind:value={searchTerm}
							type="text"
							placeholder="Busca cualquier libro... ej: Don Quijote, Harry Potter"
							class="w-full h-14 pl-12 pr-32 text-lg bg-white/20 dark:bg-white/10 backdrop-blur-xl
								border border-white/30 dark:border-white/20 rounded-2xl
								placeholder:text-muted-foreground/70 text-foreground
								focus:outline-none focus:ring-2 focus:ring-accent-500/50 focus:border-accent-500/50
								transition-all duration-300 group-hover:bg-white/30 dark:group-hover:bg-white/15"
							disabled={isSearching}
						/>
						<button
							type="submit"
							disabled={isSearching || !searchTerm.trim()}
							class="absolute right-2 top-2 h-10 px-6 bg-gradient-to-r from-accent-500 to-accent-600
								text-white rounded-xl font-medium transition-all duration-200
								hover:scale-105 hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed
								flex items-center space-x-2"
						>
							{#if isSearching}
								<Icon name="Zap" class="w-4 h-4 animate-spin" />
								<span>Buscando...</span>
							{:else}
								<Icon name="Search" class="w-4 h-4" />
								<span>Buscar</span>
							{/if}
						</button>
					</div>
				</form>
			</div>

			<!-- Estadísticas heroicas -->
			<div 
				class="grid grid-cols-2 md:grid-cols-4 gap-4 max-w-4xl mx-auto"
				transition:fly={{ y: 30, duration: 600, delay: 600, easing: quintOut }}
			>
				{#each [
					{ label: 'Libros', value: heroStats.totalBooks, iconName: 'BookOpen' },
					{ label: 'Autores', value: heroStats.totalAuthors, iconName: 'Users' },
					{ label: 'Idiomas', value: heroStats.totalLanguages, iconName: 'Globe' },
					{ label: 'Búsquedas', value: heroStats.totalSearches, iconName: 'TrendingUp' }
				] as stat, i}
					<div 
						class="glass p-4 rounded-xl border border-white/20 hover:border-white/40 transition-all duration-300 hover:scale-105"
						transition:scale={{ duration: 400, delay: 700 + (i * 100), easing: quintOut }}
					>
						<Icon name={stat.iconName} class="w-6 h-6 text-accent-500 mx-auto mb-2" />
						<div class="text-2xl font-bold text-foreground">{stat.value.toLocaleString()}</div>
						<div class="text-sm text-muted-foreground">{stat.label}</div>
					</div>
				{/each}
			</div>

			<!-- Acciones rápidas -->
			<div 
				class="flex flex-wrap justify-center gap-3"
				transition:fly={{ y: 20, duration: 500, delay: 800, easing: quintOut }}
			>
				{#each quickActions as action, i}
					<button
						onclick={action.action}
						class="px-4 py-2 bg-white/10 dark:bg-white/5 backdrop-blur-sm rounded-lg
							border border-white/20 hover:border-white/40 text-sm font-medium
							hover:bg-white/20 dark:hover:bg-white/10 transition-all duration-200
							hover:scale-105"
						transition:scale={{ duration: 300, delay: 900 + (i * 50), easing: quintOut }}
					>
						{action.label}
					</button>
				{/each}
			</div>
		</div>
	</div>

	<!-- Scroll indicator -->
	<div class="absolute bottom-8 left-1/2 transform -translate-x-1/2 animate-bounce">
		<div class="w-6 h-10 border-2 border-white/40 rounded-full flex justify-center">
			<div class="w-1 h-3 bg-white/60 rounded-full mt-2 animate-pulse"></div>
		</div>
	</div>
</section>

<!-- Features Section -->
<section class="py-20 relative">
	<div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
		<!-- Section Header -->
		<div class="text-center mb-16">
			<h2 class="text-4xl md:text-5xl font-bold mb-4 gradient-text dark:gradient-text-dark">
				Características Increíbles
			</h2>
			<p class="text-xl text-muted-foreground max-w-2xl mx-auto">
				Experimenta la gestión de libros como nunca antes con herramientas poderosas e intuitivas
			</p>
		</div>

		<!-- Features Grid -->
		<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
			{#each features as feature, i}
				<a
					href={feature.href}
					class="group block"
					transition:fly={{ y: 50, duration: 600, delay: cardDelay(i), easing: quintOut }}
				>
					<div class="relative h-full p-8 glass rounded-2xl border border-white/20 
						hover:border-white/40 transition-all duration-300 hover:scale-105 hover:-translate-y-2
						overflow-hidden group-hover:shadow-2xl">
						
						<!-- Gradient overlay -->
						<div class="absolute inset-0 bg-gradient-to-br {feature.gradient} opacity-0 
							group-hover:opacity-10 transition-opacity duration-300"></div>
						
						<!-- Icon -->
						<div class="relative z-10 mb-6">
							<div class="w-16 h-16 rounded-2xl bg-gradient-to-br {feature.gradient} 
								flex items-center justify-center transform group-hover:scale-110 transition-transform duration-300">
								<Icon name={feature.iconName} class="w-8 h-8 text-white" />
							</div>
						</div>

						<!-- Content -->
						<div class="relative z-10">
							<h3 class="text-xl font-bold mb-3 text-foreground group-hover:text-accent-600 transition-colors">
								{feature.title}
							</h3>
							<p class="text-muted-foreground mb-4 leading-relaxed">
								{feature.description}
							</p>
							<div class="flex items-center text-accent-500 font-medium group-hover:translate-x-2 transition-transform duration-300">
								<span>Explorar</span>
								<Icon name="ArrowRight" class="w-4 h-4 ml-2" />
							</div>
						</div>
					</div>
				</a>
			{/each}
		</div>
	</div>
</section>

<!-- CTA Section -->
<section class="py-20 relative overflow-hidden">
	<div class="absolute inset-0 bg-gradient-to-r from-blue-600 via-purple-600 to-indigo-600 opacity-10"></div>
	
	<div class="relative z-10 max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
		<div transition:scale={{ duration: 600, easing: quintOut }}>
			<Icon name="Sparkles" class="w-16 h-16 text-accent-500 mx-auto mb-6 animate-pulse" />
			<h2 class="text-4xl md:text-5xl font-bold mb-6">
				¿Listo para comenzar tu <span class="gradient-text dark:gradient-text-dark">aventura literaria</span>?
			</h2>
			<p class="text-xl text-muted-foreground mb-8 max-w-2xl mx-auto">
				Únete a miles de lectores que ya han descubierto una nueva forma de explorar el mundo de los libros
			</p>
			
			<div class="flex flex-col sm:flex-row gap-4 justify-center items-center">
				<a
					href="/search"
					class="inline-flex items-center px-8 py-4 bg-gradient-to-r from-accent-500 to-accent-600
						text-white font-semibold rounded-2xl shadow-lg hover:shadow-xl
						transform hover:scale-105 transition-all duration-300 space-x-2"
				>
					<Icon name="Search" class="w-5 h-5" />
					<span>Comenzar Búsqueda</span>
				</a>
				
				<a
					href="/library"
					class="inline-flex items-center px-8 py-4 glass border border-white/30
						font-semibold rounded-2xl hover:bg-white/20 dark:hover:bg-white/10
						transform hover:scale-105 transition-all duration-300 space-x-2"
				>
					<Icon name="Heart" class="w-5 h-5" />
					<span>Ver Biblioteca</span>
				</a>
			</div>
		</div>
	</div>
</section> 