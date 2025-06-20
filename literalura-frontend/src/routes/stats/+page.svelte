<script>
	import { onMount } from 'svelte';
	import { booksStore } from '$stores/books.svelte.js';
	import Icon from '../../components/Icon.svelte';
	import { fly, scale, blur, draw } from 'svelte/transition';
	import { quintOut, elasticOut, cubicOut } from 'svelte/easing';
	import { BarChart3, BookOpen, Users, TrendingUp, Globe, ArrowUp, Crown, Trophy, Sparkles, Calendar, Activity } from 'svelte-lucide';

	let isLoading = $state(false);
	let selectedPeriod = $state('all'); // 'all', 'month', 'week'
	let animationProgress = $state(0);

	// Datos derivados del store
	let books = $derived(booksStore.books);
	let topBooks = $derived(booksStore.topBooks);
	let stats = $derived(booksStore.statistics);

	// Estadísticas calculadas
	let computedStats = $derived(() => {
		const sortedBySearches = [...books].sort((a, b) => (b.searchCount || 0) - (a.searchCount || 0));
		const languageDistribution = books.reduce((acc, book) => {
			const lang = book.language || 'unknown';
			acc[lang] = (acc[lang] || 0) + 1;
			return acc;
		}, {});

		const yearDistribution = books.reduce((acc, book) => {
			if (book.firstPublishYear) {
				const decade = Math.floor(book.firstPublishYear / 10) * 10;
				acc[decade] = (acc[decade] || 0) + 1;
			}
			return acc;
		}, {});

		return {
			topSearched: sortedBySearches.slice(0, 5),
			languageDistribution,
			yearDistribution,
			averageYear: books.length > 0 
				? Math.round(books.reduce((sum, book) => sum + (book.firstPublishYear || 0), 0) / books.length)
				: 0,
			totalSearches: books.reduce((sum, book) => sum + (book.searchCount || 0), 0)
		};
	});

	// Cargar datos
	onMount(async () => {
		isLoading = true;
		try {
			await Promise.all([
				booksStore.loadAllBooks(),
				booksStore.loadTopBooks()
			]);
			
			// Animación progresiva
			const interval = setInterval(() => {
				animationProgress += 2;
				if (animationProgress >= 100) {
					clearInterval(interval);
				}
			}, 50);
		} finally {
			isLoading = false;
		}
	});

	// Utilidades para visualización
	function getLanguageFlag(language) {
		const flags = {
			'es': '🇪🇸', 'en': '🇺🇸', 'fr': '🇫🇷', 'pt': '🇧🇷',
			'de': '🇩🇪', 'it': '🇮🇹', 'ru': '🇷🇺', 'ja': '🇯🇵'
		};
		return flags[language] || '🌍';
	}

	function getLanguageName(code) {
		const names = {
			'es': 'Español', 'en': 'Inglés', 'fr': 'Francés', 'pt': 'Portugués',
			'de': 'Alemán', 'it': 'Italiano', 'ru': 'Ruso', 'ja': 'Japonés'
		};
		return names[code] || code.toUpperCase();
	}

	function getGradientForIndex(index) {
		const gradients = [
			'from-blue-500 to-cyan-500',
			'from-purple-500 to-pink-500', 
			'from-green-500 to-emerald-500',
			'from-orange-500 to-red-500',
			'from-indigo-500 to-purple-500'
		];
		return gradients[index % gradients.length];
	}

	// KPIs principales
	let mainKPIs = $derived([
		{
			title: 'Total de Libros',
			value: stats.totalBooks,
			change: '+12%',
			iconName: 'BookOpen',
			gradient: 'from-blue-500 to-cyan-500',
			description: 'Libros en biblioteca'
		},
		{
			title: 'Autores Únicos',
			value: stats.totalAuthors,
			change: '+8%',
			iconName: 'Users',
			gradient: 'from-purple-500 to-pink-500',
			description: 'Autores diferentes'
		},
		{
			title: 'Búsquedas Totales',
			value: computedStats.totalSearches,
			change: '+25%',
			iconName: 'TrendingUp',
			gradient: 'from-green-500 to-emerald-500',
			description: 'Consultas realizadas'
		},
		{
			title: 'Idiomas',
			value: stats.totalLanguages,
			change: '+3%',
			iconName: 'Globe',
			gradient: 'from-orange-500 to-red-500',
			description: 'Idiomas disponibles'
		}
	]);
</script>

<svelte:head>
	<title>Estadísticas - LiterAlura</title>
	<meta name="description" content="Análisis detallado y estadísticas de tu biblioteca" />
</svelte:head>

<div class="min-h-screen py-8">
	<div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
		<!-- Header -->
		<div class="text-center mb-12">
			<div transition:scale={{ duration: 800, easing: elasticOut }} class="mb-6">
				<div class="inline-flex items-center justify-center w-20 h-20 rounded-full bg-gradient-to-br from-orange-500 to-red-600 shadow-2xl">
					<BarChart3 class="w-10 h-10 text-white animate-bounce" />
				</div>
			</div>
			
			<h1 class="text-5xl md:text-6xl font-bold mb-4 gradient-text dark:gradient-text-dark">
				Analytics
			</h1>
			<p class="text-xl text-muted-foreground max-w-2xl mx-auto">
				Descubre patrones y tendencias en tu universo literario
			</p>
		</div>

		<!-- KPIs Principales -->
		<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
			{#each mainKPIs as kpi, i}
				<div 
					class="relative group glass rounded-2xl border border-white/20 hover:border-white/40 
						p-6 transition-all duration-500 hover:scale-105 hover:-translate-y-2 overflow-hidden"
					transition:fly={{ y: 50, duration: 600, delay: i * 100, easing: quintOut }}
				>
					<!-- Animated background -->
					<div class="absolute inset-0 bg-gradient-to-br {kpi.gradient} opacity-0 
						group-hover:opacity-10 transition-opacity duration-500"></div>
					
					<!-- Progress bar -->
					<div class="absolute bottom-0 left-0 h-1 bg-gradient-to-r {kpi.gradient} 
						transition-all duration-1000 ease-out"
						style="width: {animationProgress}%"></div>

					<!-- Content -->
					<div class="relative z-10">
						<!-- Icon -->
						<div class="flex items-center justify-between mb-4">
							<div class="w-12 h-12 rounded-xl bg-gradient-to-br {kpi.gradient} 
								flex items-center justify-center transform group-hover:scale-110 transition-transform duration-300">
								<Icon name={kpi.iconName} class="w-6 h-6 text-white" />
							</div>
							<div class="flex items-center text-green-500 text-sm font-medium">
								<ArrowUp class="w-4 h-4 mr-1" />
								{kpi.change}
							</div>
						</div>

						<!-- Value -->
						<div class="text-3xl font-bold text-foreground mb-1 transition-all duration-300 group-hover:text-accent-600">
							{#if kpi.value > 0}
								<span class="counter">{kpi.value.toLocaleString()}</span>
							{:else}
								<span class="text-muted-foreground">-</span>
							{/if}
						</div>

						<!-- Label -->
						<div class="text-sm font-medium text-foreground/80 mb-1">
							{kpi.title}
						</div>
						<div class="text-xs text-muted-foreground">
							{kpi.description}
						</div>
					</div>
				</div>
			{/each}
		</div>

		<!-- Charts Section -->
		<div class="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-12">
			<!-- Top Books Chart -->
			<div class="glass rounded-2xl border border-white/20 p-8" 
				transition:fly={{ x: -50, duration: 600, delay: 200, easing: quintOut }}>
				
				<div class="flex items-center justify-between mb-6">
					<div class="flex items-center space-x-3">
						<Crown class="w-6 h-6 text-amber-500" />
						<h3 class="text-xl font-bold text-foreground">Top 5 Libros Más Buscados</h3>
					</div>
					<Trophy class="w-5 h-5 text-amber-500" />
				</div>

				{#if computedStats.topSearched.length > 0}
					<div class="space-y-4">
						{#each computedStats.topSearched as book, i}
							<div class="group" transition:fly={{ x: -30, duration: 400, delay: 300 + (i * 100), easing: quintOut }}>
								<div class="flex items-center space-x-4 p-4 rounded-xl bg-white/5 hover:bg-white/10 transition-all duration-300">
									<!-- Ranking -->
									<div class="flex-shrink-0 w-8 h-8 rounded-full bg-gradient-to-br {getGradientForIndex(i)} 
										flex items-center justify-center text-white font-bold text-sm">
										{i + 1}
									</div>

									<!-- Book info -->
									<div class="flex-1 min-w-0">
										<h4 class="font-medium text-foreground truncate group-hover:text-accent-600 transition-colors">
											{book.title}
										</h4>
										<p class="text-sm text-muted-foreground truncate">
											{book.author?.name || 'Autor desconocido'}
										</p>
									</div>

									<!-- Search count -->
									<div class="flex-shrink-0 text-right">
										<div class="text-lg font-bold text-foreground">
											{book.searchCount || 0}
										</div>
										<div class="text-xs text-muted-foreground">búsquedas</div>
									</div>

									<!-- Progress bar -->
									<div class="flex-shrink-0 w-20">
										<div class="h-2 bg-white/10 rounded-full overflow-hidden">
											<div class="h-full bg-gradient-to-r {getGradientForIndex(i)} rounded-full transition-all duration-1000 ease-out"
												style="width: {Math.min((book.searchCount || 0) / Math.max(computedStats.topSearched[0]?.searchCount || 1, 1) * 100, 100)}%"></div>
										</div>
									</div>
								</div>
							</div>
						{/each}
					</div>
				{:else}
					<div class="text-center py-12">
						<BookOpen class="w-16 h-16 text-muted-foreground/30 mx-auto mb-4" />
						<p class="text-muted-foreground">No hay datos de búsquedas disponibles</p>
					</div>
				{/if}
			</div>

			<!-- Languages Distribution -->
			<div class="glass rounded-2xl border border-white/20 p-8"
				transition:fly={{ x: 50, duration: 600, delay: 200, easing: quintOut }}>
				
				<div class="flex items-center justify-between mb-6">
					<div class="flex items-center space-x-3">
						<Globe class="w-6 h-6 text-blue-500" />
						<h3 class="text-xl font-bold text-foreground">Distribución por Idiomas</h3>
					</div>
					<Sparkles class="w-5 h-5 text-blue-500 animate-pulse" />
				</div>

				{#if Object.keys(computedStats.languageDistribution).length > 0}
					<div class="space-y-4">
						{#each Object.entries(computedStats.languageDistribution).sort(([,a], [,b]) => b - a).slice(0, 6) as [language, count], i}
							<div class="group" transition:fly={{ x: 30, duration: 400, delay: 300 + (i * 100), easing: quintOut }}>
								<div class="flex items-center justify-between mb-2">
									<div class="flex items-center space-x-3">
										<span class="text-2xl">{getLanguageFlag(language)}</span>
										<span class="font-medium text-foreground group-hover:text-accent-600 transition-colors">
											{getLanguageName(language)}
										</span>
									</div>
									<div class="text-sm text-muted-foreground">
										{count} libro{count !== 1 ? 's' : ''} ({Math.round(count / books.length * 100)}%)
									</div>
								</div>
								<div class="h-3 bg-white/10 rounded-full overflow-hidden">
									<div class="h-full bg-gradient-to-r {getGradientForIndex(i)} rounded-full transition-all duration-1000 ease-out"
										style="width: {Math.min(count / books.length * 100, 100)}%"></div>
								</div>
							</div>
						{/each}
					</div>
				{:else}
					<div class="text-center py-12">
						<Globe class="w-16 h-16 text-muted-foreground/30 mx-auto mb-4" />
						<p class="text-muted-foreground">No hay datos de idiomas disponibles</p>
					</div>
				{/if}
			</div>
		</div>

		<!-- Timeline & Insights -->
		<div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
			<!-- Timeline por décadas -->
			<div class="lg:col-span-2 glass rounded-2xl border border-white/20 p-8"
				transition:fly={{ y: 50, duration: 600, delay: 400, easing: quintOut }}>
				
				<div class="flex items-center justify-between mb-6">
					<div class="flex items-center space-x-3">
						<Calendar class="w-6 h-6 text-purple-500" />
						<h3 class="text-xl font-bold text-foreground">Timeline por Décadas</h3>
					</div>
					<Activity class="w-5 h-5 text-purple-500" />
				</div>

				{#if Object.keys(computedStats.yearDistribution).length > 0}
					<div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
						{#each Object.entries(computedStats.yearDistribution).sort(([a], [b]) => Number(b) - Number(a)).slice(0, 8) as [decade, count], i}
							<div class="group p-4 rounded-xl bg-white/5 hover:bg-white/10 transition-all duration-300 text-center"
								transition:scale={{ duration: 400, delay: 500 + (i * 50), easing: quintOut }}>
								
								<div class="text-2xl font-bold text-foreground mb-1 group-hover:text-accent-600 transition-colors">
									{count}
								</div>
								<div class="text-sm text-muted-foreground mb-2">
									{decade}s
								</div>
								<div class="h-1 bg-white/10 rounded-full overflow-hidden">
									<div class="h-full bg-gradient-to-r from-purple-500 to-pink-500 rounded-full transition-all duration-1000 ease-out"
										style="width: {Math.min(count / Math.max(...Object.values(computedStats.yearDistribution)) * 100, 100)}%"></div>
								</div>
							</div>
						{/each}
					</div>
				{:else}
					<div class="text-center py-12">
						<Calendar class="w-16 h-16 text-muted-foreground/30 mx-auto mb-4" />
						<p class="text-muted-foreground">No hay datos de años disponibles</p>
					</div>
				{/if}
			</div>

			<!-- Quick Insights -->
			<div class="space-y-6">
				<!-- Insight 1 -->
				<div class="glass rounded-2xl border border-white/20 p-6"
					transition:fly={{ x: 50, duration: 600, delay: 600, easing: quintOut }}>
					
					<div class="flex items-center space-x-3 mb-4">
						<Target class="w-6 h-6 text-green-500" />
						<h4 class="font-bold text-foreground">Promedio de Año</h4>
					</div>
					
					<div class="text-3xl font-bold text-foreground mb-2">
						{computedStats.averageYear || 'N/A'}
					</div>
					<p class="text-sm text-muted-foreground">
						Año promedio de publicación de tu biblioteca
					</p>
				</div>

				<!-- Insight 2 -->
				<div class="glass rounded-2xl border border-white/20 p-6"
					transition:fly={{ x: 50, duration: 600, delay: 700, easing: quintOut }}>
					
					<div class="flex items-center space-x-3 mb-4">
						<Zap class="w-6 h-6 text-yellow-500" />
						<h4 class="font-bold text-foreground">Actividad</h4>
					</div>
					
					<div class="text-3xl font-bold text-foreground mb-2">
						{computedStats.totalSearches > 0 ? 'Alta' : 'Baja'}
					</div>
					<p class="text-sm text-muted-foreground">
						Nivel de actividad de búsqueda
					</p>
				</div>

				<!-- Insight 3 -->
				<div class="glass rounded-2xl border border-white/20 p-6"
					transition:fly={{ x: 50, duration: 600, delay: 800, easing: quintOut }}>
					
					<div class="flex items-center space-x-3 mb-4">
						<Star class="w-6 h-6 text-purple-500" />
						<h4 class="font-bold text-foreground">Diversidad</h4>
					</div>
					
					<div class="text-3xl font-bold text-foreground mb-2">
						{Object.keys(computedStats.languageDistribution).length}
					</div>
					<p class="text-sm text-muted-foreground">
						Idiomas diferentes en tu colección
					</p>
				</div>
			</div>
		</div>
	</div>
</div>

<style>
	.counter {
		animation: countUp 2s ease-out;
	}

	@keyframes countUp {
		from { opacity: 0; transform: translateY(20px); }
		to { opacity: 1; transform: translateY(0); }
	}
</style> 