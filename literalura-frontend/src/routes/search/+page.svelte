<script>
	import { onMount } from 'svelte';
	import { page } from '$app/stores';
	import { booksStore } from '$stores/books.svelte.js';
	import { apiStore } from '$stores/api.svelte.js';
	import Icon from '../../components/Icon.svelte';
	import { fly, scale, blur, slide } from 'svelte/transition';
	import { quintOut, elasticOut } from 'svelte/easing';
	import { Search, Filter, Loader } from 'svelte-lucide';

	let searchTerm = $state('');
	let searchResults = $state([]);
	let isSearching = $state(false);
	let showFilters = $state(false);
	let searchHistory = $state([]);
	
	// Filtros avanzados
	let filters = $state({
		language: '',
		author: '',
		yearFrom: '',
		yearTo: '',
		sortBy: 'relevance'
	});

	// Sugerencias de búsqueda populares
	const popularSearches = [
		'Don Quijote', 'Harry Potter', 'Cien años de soledad', 
		'El Señor de los Anillos', 'Pride and Prejudice', 
		'To Kill a Mockingbird', 'Sapiens', 'Dune'
	];

	// Efectos de escritura animada
	let placeholderIndex = $state(0);
	let displayPlaceholder = $state('');
	let isTyping = $state(false);

	// Animación del placeholder
	function animatePlaceholder() {
		const placeholders = [
			'Busca por título...',
			'Busca por autor...',
			'Busca por ISBN...',
			'Descubre nuevos mundos...'
		];
		
		if (!isTyping) {
			const text = placeholders[placeholderIndex];
			let i = 0;
			
			const typeInterval = setInterval(() => {
				displayPlaceholder = text.slice(0, i);
				i++;
				
				if (i > text.length) {
					clearInterval(typeInterval);
					setTimeout(() => {
						const deleteInterval = setInterval(() => {
							displayPlaceholder = text.slice(0, i);
							i--;
							
							if (i < 0) {
								clearInterval(deleteInterval);
								placeholderIndex = (placeholderIndex + 1) % placeholders.length;
								setTimeout(animatePlaceholder, 500);
							}
						}, 50);
					}, 2000);
				}
			}, 100);
		}
	}

	// Búsqueda en tiempo real con debounce
	let searchTimeout;
	function debounceSearch() {
		clearTimeout(searchTimeout);
		searchTimeout = setTimeout(performSearch, 300);
	}

	async function performSearch() {
		if (!searchTerm.trim()) {
			searchResults = [];
			return;
		}

		isSearching = true;
		try {
			const result = await booksStore.searchBook(searchTerm);
			searchResults = result ? [result] : [];
			
			// Agregar a historial
			if (result && !searchHistory.some(h => h.term === searchTerm)) {
				searchHistory = [
					{ term: searchTerm, timestamp: Date.now(), result: result },
					...searchHistory.slice(0, 4) // Mantener solo 5 búsquedas
				];
			}
		} catch (error) {
			console.error('Error en búsqueda:', error);
			searchResults = [];
		} finally {
			isSearching = false;
		}
	}

	// Búsqueda por sugerencia
	function searchSuggestion(term) {
		searchTerm = term;
		performSearch();
	}

	onMount(() => {
		// Leer parámetro de URL si existe
		const urlSearch = $page.url.searchParams.get('q');
		if (urlSearch) {
			searchTerm = urlSearch;
			performSearch();
		}
		
		// Iniciar animación del placeholder
		animatePlaceholder();
	});

	// Reactividad para búsqueda en tiempo real
	$effect(() => {
		if (searchTerm) {
			isTyping = true;
			debounceSearch();
		} else {
			isTyping = false;
		}
	});
</script>

<svelte:head>
	<title>Búsqueda - LiterAlura</title>
	<meta name="description" content="Busca libros en la vasta colección de OpenLibrary" />
</svelte:head>

<div class="min-h-screen py-8">
	<div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
		<!-- Header de búsqueda -->
		<div class="text-center mb-12">
			<h1 class="text-5xl md:text-6xl font-bold mb-4 gradient-text dark:gradient-text-dark">
				Explora el Universo
			</h1>
			<p class="text-xl text-muted-foreground">
				Encuentra cualquier libro de la vasta colección de OpenLibrary
			</p>
		</div>

		<!-- Barra de búsqueda principal -->
		<div class="max-w-4xl mx-auto mb-8">
			<div class="relative group" transition:fly={{ y: -20, duration: 600, easing: quintOut }}>
				<!-- Input principal -->
				<div class="relative">
					<Search class="absolute left-6 top-1/2 transform -translate-y-1/2 w-6 h-6 text-muted-foreground group-focus-within:text-accent-500 transition-colors z-10" />
					
					<input
						bind:value={searchTerm}
						type="text"
						placeholder={displayPlaceholder || 'Busca libros, autores, ISBN...'}
						class="w-full h-16 pl-16 pr-32 text-lg bg-white/30 dark:bg-white/10 backdrop-blur-xl
							border-2 border-white/30 dark:border-white/20 rounded-2xl
							placeholder:text-muted-foreground/70 text-foreground
							focus:outline-none focus:ring-2 focus:ring-accent-500/50 focus:border-accent-500/50
							transition-all duration-300 group-hover:bg-white/40 dark:group-hover:bg-white/15
							shadow-2xl hover:shadow-accent-500/10"
						disabled={isSearching}
					/>

					<!-- Botón de filtros -->
					<button
						onclick={() => showFilters = !showFilters}
						class="absolute right-20 top-1/2 transform -translate-y-1/2 p-2 rounded-lg
							bg-white/20 dark:bg-white/10 hover:bg-white/30 dark:hover:bg-white/20
							transition-all duration-200 hover:scale-110 z-10
							{showFilters ? 'bg-accent-500 text-white' : ''}"
						aria-label="Filtros"
					>
						<Filter class="w-5 h-5" />
					</button>

					<!-- Indicador de carga -->
					{#if isSearching}
						<div class="absolute right-4 top-1/2 transform -translate-y-1/2 z-10">
							<Loader class="w-6 h-6 text-accent-500 animate-spin" />
						</div>
					{/if}
				</div>

				<!-- Panel de filtros -->
				{#if showFilters}
					<div 
						class="absolute top-full mt-4 w-full glass rounded-2xl border border-white/20 p-6 z-20"
						transition:slide={{ duration: 300, easing: quintOut }}
					>
						<div class="grid grid-cols-1 md:grid-cols-3 gap-4">
							<div>
								<label for="language-filter" class="block text-sm font-medium text-foreground mb-2">Idioma</label>
								<select 
									id="language-filter"
									bind:value={filters.language}
									class="w-full p-3 bg-white/20 dark:bg-white/10 border border-white/20 rounded-lg"
								>
									<option value="">Todos los idiomas</option>
									<option value="es">Español</option>
									<option value="en">Inglés</option>
									<option value="fr">Francés</option>
									<option value="pt">Portugués</option>
								</select>
							</div>
							
							<div>
								<label for="year-filter" class="block text-sm font-medium text-foreground mb-2">Año desde</label>
								<input 
									id="year-filter"
									bind:value={filters.yearFrom}
									type="number" 
									placeholder="1900"
									class="w-full p-3 bg-white/20 dark:bg-white/10 border border-white/20 rounded-lg"
								/>
							</div>
							
							<div>
								<label for="sort-filter" class="block text-sm font-medium text-foreground mb-2">Ordenar por</label>
								<select 
									id="sort-filter"
									bind:value={filters.sortBy}
									class="w-full p-3 bg-white/20 dark:bg-white/10 border border-white/20 rounded-lg"
								>
									<option value="relevance">Relevancia</option>
									<option value="year">Año</option>
									<option value="title">Título</option>
									<option value="author">Autor</option>
								</select>
							</div>
						</div>
					</div>
				{/if}
			</div>
		</div>

		<!-- Sugerencias populares -->
		{#if !searchTerm && !isSearching}
			<div class="max-w-4xl mx-auto mb-12" transition:fly={{ y: 20, duration: 400, easing: quintOut }}>
				<h3 class="text-lg font-semibold text-foreground mb-4 text-center">Búsquedas populares</h3>
				<div class="flex flex-wrap justify-center gap-3">
					{#each popularSearches as suggestion, i}
						<button
							onclick={() => searchSuggestion(suggestion)}
							class="px-4 py-2 bg-white/20 dark:bg-white/10 backdrop-blur-sm rounded-full
								border border-white/20 hover:border-accent-500/50 text-sm font-medium
								hover:bg-accent-500/10 transition-all duration-200 hover:scale-105"
							transition:scale={{ duration: 200, delay: i * 50, easing: quintOut }}
						>
							{suggestion}
						</button>
					{/each}
				</div>
			</div>
		{/if}

		<!-- Historial de búsquedas -->
		{#if searchHistory.length > 0 && !searchTerm}
			<div class="max-w-4xl mx-auto mb-12" transition:fly={{ y: 20, duration: 400, easing: quintOut }}>
				<h3 class="text-lg font-semibold text-foreground mb-4">Búsquedas recientes</h3>
				<div class="space-y-2">
					{#each searchHistory as item, i}
						<button
							onclick={() => searchSuggestion(item.term)}
							class="w-full p-4 glass rounded-xl border border-white/20 hover:border-white/40
								transition-all duration-200 hover:scale-[1.02] text-left group"
							transition:slide={{ duration: 200, delay: i * 50, easing: quintOut }}
						>
							<div class="flex items-center justify-between">
								<div class="flex items-center space-x-3">
									<Search class="w-4 h-4 text-muted-foreground" />
									<span class="font-medium text-foreground">{item.term}</span>
								</div>
								<div class="text-xs text-muted-foreground">
									{new Date(item.timestamp).toLocaleDateString()}
								</div>
							</div>
						</button>
					{/each}
				</div>
			</div>
		{/if}

		<!-- Resultados de búsqueda -->
		{#if searchResults.length > 0}
			<div class="max-w-6xl mx-auto">
				<h2 class="text-2xl font-bold text-foreground mb-8">
					Resultados de búsqueda para "{searchTerm}"
				</h2>
				
				<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
					{#each searchResults as book, i}
						<div 
							class="group glass rounded-2xl border border-white/20 hover:border-white/40 
								overflow-hidden transition-all duration-300 hover:scale-105 hover:-translate-y-2
								hover:shadow-2xl"
							transition:fly={{ y: 50, duration: 600, delay: i * 100, easing: quintOut }}
						>
							<!-- Imagen del libro -->
							<div class="relative h-64 bg-gradient-to-br from-accent-100 to-accent-200 dark:from-accent-900 dark:to-accent-800">
								{#if book.coverId}
									<img 
										src="https://covers.openlibrary.org/b/id/{book.coverId}-L.jpg" 
										alt="Portada de {book.title}"
										class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
									/>
								{:else}
									<div class="w-full h-full flex items-center justify-center">
										<BookOpen class="w-16 h-16 text-accent-500/50" />
									</div>
								{/if}
								
								<!-- Overlay con información rápida -->
								<div class="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent
									opacity-0 group-hover:opacity-100 transition-opacity duration-300">
									<div class="absolute bottom-4 left-4 right-4">
										<div class="flex items-center space-x-2 text-white text-sm">
											<Star class="w-4 h-4 fill-yellow-400 text-yellow-400" />
											<span>Búsquedas: {book.searchCount || 0}</span>
										</div>
									</div>
								</div>
							</div>

							<!-- Contenido del libro -->
							<div class="p-6">
								<h3 class="text-xl font-bold text-foreground mb-2 line-clamp-2 group-hover:text-accent-600 transition-colors">
									{book.title}
								</h3>
								
								{#if book.author}
									<div class="flex items-center space-x-2 text-muted-foreground mb-3">
										<User class="w-4 h-4" />
										<span class="text-sm">{book.author.name}</span>
									</div>
								{/if}

								<div class="flex items-center justify-between text-sm text-muted-foreground">
									{#if book.firstPublishYear}
										<div class="flex items-center space-x-1">
											<Calendar class="w-4 h-4" />
											<span>{book.firstPublishYear}</span>
										</div>
									{/if}
									
									{#if book.language}
										<div class="flex items-center space-x-1">
											<Globe class="w-4 h-4" />
											<span class="uppercase">{book.language}</span>
										</div>
									{/if}
								</div>

								<!-- Botón de acción -->
								<button class="w-full mt-4 px-4 py-2 bg-accent-500 hover:bg-accent-600 
									text-white rounded-lg font-medium transition-all duration-200
									transform hover:scale-105 opacity-0 group-hover:opacity-100">
									Ver Detalles
								</button>
							</div>
						</div>
					{/each}
				</div>
			</div>
		{:else if searchTerm && !isSearching}
			<!-- Sin resultados -->
			<div class="text-center py-16" transition:fade={{ duration: 400 }}>
				<BookOpen class="w-24 h-24 text-muted-foreground/50 mx-auto mb-6" />
				<h3 class="text-2xl font-semibold text-foreground mb-2">No se encontraron resultados</h3>
				<p class="text-muted-foreground">
					Intenta con otros términos de búsqueda o explora nuestras sugerencias
				</p>
			</div>
		{/if}
	</div>
</div> 