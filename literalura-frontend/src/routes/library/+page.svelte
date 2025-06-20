<script>
	import { onMount } from 'svelte';
	import { booksStore } from '$stores/books.svelte.js';
	import { 
		BookOpen, Filter, Grid3X3, List, Search, SortAsc, SortDesc,
		Globe, Calendar, User, TrendingUp, Eye, Heart, Download,
		LayoutGrid, LayoutList, Zap, Sparkles
	} from 'svelte-lucide';
	import Icon from '../../components/Icon.svelte';
	import { fly, scale, fade } from 'svelte/transition';
	import { quintOut, elasticOut } from 'svelte/easing';
	
	let viewMode = $state('grid'); // 'grid' | 'list'
	let sortBy = $state('searchCount');
	let sortOrder = $state('desc');
	let filterBy = $state('all'); // 'all' | 'language' | 'author' | 'year'
	let searchFilter = $state('');
	let selectedLanguage = $state('');
	let isLoading = $state(false);

	// Datos derivados
	let books = $derived(booksStore.filteredBooks);
	let stats = $derived(booksStore.statistics);
	
	// Filtrar libros según criterios activos
	let filteredBooks = $derived(() => {
		let filtered = [...books];

		// Filtro por texto
		if (searchFilter) {
			const term = searchFilter.toLowerCase();
			filtered = filtered.filter(book =>
				book.title?.toLowerCase().includes(term) ||
				book.author?.name?.toLowerCase().includes(term)
			);
		}

		// Filtro por idioma
		if (selectedLanguage) {
			filtered = filtered.filter(book => 
				book.language?.toLowerCase() === selectedLanguage.toLowerCase()
			);
		}

		// Ordenar
		filtered.sort((a, b) => {
			let aVal = a[sortBy] || 0;
			let bVal = b[sortBy] || 0;
			
			if (sortBy === 'title' || sortBy === 'author.name') {
				aVal = sortBy === 'title' ? a.title : a.author?.name || '';
				bVal = sortBy === 'title' ? b.title : b.author?.name || '';
				return sortOrder === 'desc' 
					? bVal.localeCompare(aVal)
					: aVal.localeCompare(bVal);
			}
			
			return sortOrder === 'desc' ? bVal - aVal : aVal - bVal;
		});

		return filtered;
	});

	// Configuraciones de vista
	const viewModes = [
		{ id: 'grid', icon: LayoutGrid, label: 'Cuadrícula' },
		{ id: 'list', icon: LayoutList, label: 'Lista' }
	];

	const sortOptions = [
		{ value: 'searchCount', label: 'Más buscados', icon: TrendingUp },
		{ value: 'title', label: 'Título A-Z', icon: BookOpen },
		{ value: 'firstPublishYear', label: 'Año publicación', icon: Calendar },
		{ value: 'author.name', label: 'Autor A-Z', icon: User }
	];

	// Cargar datos al montar
	onMount(async () => {
		isLoading = true;
		try {
			await booksStore.loadAllBooks();
		} finally {
			isLoading = false;
		}
	});

	// Funciones de utilidad
	function toggleSort() {
		sortOrder = sortOrder === 'desc' ? 'asc' : 'desc';
	}

	function getLanguageFlag(language) {
		const flags = {
			'es': '🇪🇸',
			'en': '🇺🇸', 
			'fr': '🇫🇷',
			'pt': '🇧🇷',
			'de': '🇩🇪',
			'it': '🇮🇹'
		};
		return flags[language] || '🌍';
	}

	// Estadísticas destacadas para la biblioteca
	let featuredStats = $derived([
		{ 
			label: 'Total Libros', 
			value: stats.totalBooks, 
			icon: BookOpen, 
			color: 'from-blue-500 to-cyan-500',
			description: 'En tu biblioteca'
		},
		{ 
			label: 'Autores Únicos', 
			value: stats.totalAuthors, 
			icon: User, 
			color: 'from-purple-500 to-pink-500',
			description: 'Diferentes autores'
		},
		{ 
			label: 'Idiomas', 
			value: stats.totalLanguages, 
			icon: Globe, 
			color: 'from-green-500 to-emerald-500',
			description: 'Idiomas disponibles'
		},
		{ 
			label: 'Total Búsquedas', 
			value: stats.totalSearches, 
			icon: TrendingUp, 
			color: 'from-orange-500 to-red-500',
			description: 'Consultas realizadas'
		}
	]);
</script>

<svelte:head>
	<title>Biblioteca - LiterAlura</title>
	<meta name="description" content="Explora y gestiona tu biblioteca personal de libros" />
</svelte:head>

<div class="min-h-screen py-8">
	<div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
		<!-- Header principal -->
		<div class="text-center mb-12">
			<div transition:scale={{ duration: 800, easing: elasticOut }} class="mb-6">
				<div class="inline-flex items-center justify-center w-20 h-20 rounded-full bg-gradient-to-br from-purple-500 to-pink-600 shadow-2xl">
					<BookOpen class="w-10 h-10 text-white animate-pulse" />
				</div>
			</div>
			
			<h1 class="text-5xl md:text-6xl font-bold mb-4 gradient-text dark:gradient-text-dark">
				Tu Biblioteca
			</h1>
			<p class="text-xl text-muted-foreground max-w-2xl mx-auto">
				Gestiona, explora y descubre patrones en tu colección personal de libros
			</p>
		</div>

		<!-- Estadísticas destacadas -->
		<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
			{#each featuredStats as stat, i}
				<div 
					class="relative group glass rounded-2xl border border-white/20 hover:border-white/40 
						p-6 transition-all duration-300 hover:scale-105 hover:-translate-y-1 overflow-hidden"
					transition:fly={{ y: 50, duration: 600, delay: i * 100, easing: quintOut }}
				>
					<!-- Gradient overlay -->
					<div class="absolute inset-0 bg-gradient-to-br {stat.color} opacity-0 
						group-hover:opacity-10 transition-opacity duration-300"></div>
					
					<!-- Icon -->
					<div class="relative z-10 mb-4">
						<div class="w-12 h-12 rounded-xl bg-gradient-to-br {stat.color} 
							flex items-center justify-center transform group-hover:scale-110 transition-transform duration-300">
							<stat.icon class="w-6 h-6 text-white" />
						</div>
					</div>

					<!-- Content -->
					<div class="relative z-10">
						<div class="text-3xl font-bold text-foreground mb-1">
							{stat.value.toLocaleString()}
						</div>
						<div class="text-sm font-medium text-foreground/80 mb-1">
							{stat.label}
						</div>
						<div class="text-xs text-muted-foreground">
							{stat.description}
						</div>
					</div>
				</div>
			{/each}
		</div>

		<!-- Controles de filtrado y vista -->
		<div class="glass rounded-2xl border border-white/20 p-6 mb-8" 
			transition:fly={{ y: -20, duration: 600, easing: quintOut }}>
			
			<div class="flex flex-col lg:flex-row gap-6 items-center justify-between">
				<!-- Búsqueda -->
				<div class="relative flex-1 max-w-md group">
					<Search class="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-muted-foreground 
						group-focus-within:text-accent-500 transition-colors" />
					<input
						bind:value={searchFilter}
						type="text"
						placeholder="Buscar en biblioteca..."
						class="w-full pl-10 pr-4 py-3 bg-white/20 dark:bg-white/10 border border-white/20 rounded-xl
							focus:outline-none focus:ring-2 focus:ring-accent-500/50 focus:border-accent-500/50
							transition-all duration-200 text-foreground placeholder:text-muted-foreground/70"
					/>
				</div>

				<!-- Filtros -->
				<div class="flex items-center space-x-4">
					<!-- Selector de idioma -->
					<select 
						bind:value={selectedLanguage}
						class="px-4 py-3 bg-white/20 dark:bg-white/10 border border-white/20 rounded-xl
							focus:outline-none focus:ring-2 focus:ring-accent-500/50 text-foreground"
					>
						<option value="">Todos los idiomas</option>
						{#each stats.languages as language}
							<option value={language}>
								{getLanguageFlag(language)} {language.toUpperCase()}
							</option>
						{/each}
					</select>

					<!-- Ordenamiento -->
					<select 
						bind:value={sortBy}
						class="px-4 py-3 bg-white/20 dark:bg-white/10 border border-white/20 rounded-xl
							focus:outline-none focus:ring-2 focus:ring-accent-500/50 text-foreground"
					>
						{#each sortOptions as option}
							<option value={option.value}>{option.label}</option>
						{/each}
					</select>

					<!-- Botón orden -->
					<button
						onclick={toggleSort}
						class="p-3 bg-white/20 dark:bg-white/10 border border-white/20 rounded-xl
							hover:bg-white/30 dark:hover:bg-white/20 transition-all duration-200
							hover:scale-110"
						aria-label="Cambiar orden"
					>
						{#if sortOrder === 'desc'}
							<SortDesc class="w-5 h-5 text-foreground" />
						{:else}
							<SortAsc class="w-5 h-5 text-foreground" />
						{/if}
					</button>

					<!-- Separador -->
					<div class="w-px h-8 bg-white/20"></div>

					<!-- Modo de vista -->
					<div class="flex bg-white/10 rounded-xl p-1">
						{#each viewModes as mode}
							<button
								onclick={() => viewMode = mode.id}
								class="p-2 rounded-lg transition-all duration-200 relative overflow-hidden
									{viewMode === mode.id ? 'bg-accent-500 text-white' : 'text-muted-foreground hover:text-foreground'}"
								aria-label={mode.label}
							>
								<mode.icon class="w-5 h-5" />
							</button>
						{/each}
					</div>
				</div>
			</div>

			<!-- Contador de resultados -->
			<div class="mt-4 pt-4 border-t border-white/20">
				<div class="flex items-center justify-between text-sm">
					<span class="text-muted-foreground">
						{filteredBooks.length} de {books.length} libros
						{#if searchFilter || selectedLanguage}
							<span class="text-accent-500 font-medium">(filtrados)</span>
						{/if}
					</span>
					
					{#if searchFilter || selectedLanguage}
						<button
							onclick={() => { searchFilter = ''; selectedLanguage = ''; }}
							class="text-accent-500 hover:text-accent-600 font-medium transition-colors"
						>
							Limpiar filtros
						</button>
					{/if}
				</div>
			</div>
		</div>

		<!-- Grid/Lista de libros -->
		{#if isLoading}
			<!-- Skeleton loader -->
			<div class="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6">
				{#each Array(8) as _, i}
					<div class="glass rounded-2xl border border-white/20 overflow-hidden animate-pulse">
						<div class="h-64 bg-white/10"></div>
						<div class="p-4 space-y-3">
							<div class="h-4 bg-white/10 rounded"></div>
							<div class="h-3 bg-white/10 rounded w-3/4"></div>
							<div class="h-3 bg-white/10 rounded w-1/2"></div>
						</div>
					</div>
				{/each}
			</div>
		{:else if filteredBooks.length === 0}
			<!-- Estado vacío -->
			<div class="text-center py-20" transition:fade={{ duration: 400 }}>
				<div class="mb-6">
					<BookOpen class="w-24 h-24 text-muted-foreground/30 mx-auto mb-4" />
					<Sparkles class="w-8 h-8 text-accent-500 mx-auto animate-pulse" />
				</div>
				<h3 class="text-2xl font-semibold text-foreground mb-2">
					{searchFilter || selectedLanguage ? 'No se encontraron resultados' : 'Tu biblioteca está vacía'}
				</h3>
				<p class="text-muted-foreground mb-6">
					{searchFilter || selectedLanguage 
						? 'Intenta ajustar los filtros de búsqueda' 
						: 'Comienza buscando y agregando libros a tu colección'}
				</p>
				<a
					href="/search"
					class="inline-flex items-center px-6 py-3 bg-gradient-to-r from-accent-500 to-accent-600
						text-white font-semibold rounded-2xl shadow-lg hover:shadow-xl
						transform hover:scale-105 transition-all duration-300 space-x-2"
				>
					<Search class="w-5 h-5" />
					<span>Buscar Libros</span>
				</a>
			</div>
		{:else}
			<!-- Vista Grid -->
			{#if viewMode === 'grid'}
				<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
					{#each filteredBooks as book, i}
						<div 
							class="group glass rounded-2xl border border-white/20 hover:border-white/40 
								overflow-hidden transition-all duration-300 hover:scale-105 hover:-translate-y-2
								hover:shadow-2xl cursor-pointer"
							transition:fly={{ y: 50, duration: 600, delay: Math.min(i * 50, 500), easing: quintOut }}
						>
							<!-- Imagen del libro -->
							<div class="relative h-48 bg-gradient-to-br from-accent-100 to-accent-200 dark:from-accent-900 dark:to-accent-800">
								{#if book.coverId}
									<img 
										src="https://covers.openlibrary.org/b/id/{book.coverId}-M.jpg" 
										alt="Portada de {book.title}"
										class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
									/>
								{:else}
									<div class="w-full h-full flex items-center justify-center">
										<BookOpen class="w-12 h-12 text-accent-500/50" />
									</div>
								{/if}
								
								<!-- Badges -->
								<div class="absolute top-2 right-2 flex flex-col space-y-1">
									{#if book.searchCount > 0}
										<div class="px-2 py-1 bg-black/50 backdrop-blur-sm rounded-full text-xs text-white flex items-center space-x-1">
											<Eye class="w-3 h-3" />
											<span>{book.searchCount}</span>
										</div>
									{/if}
									{#if book.language}
										<div class="px-2 py-1 bg-black/50 backdrop-blur-sm rounded-full text-xs text-white">
											{getLanguageFlag(book.language)}
										</div>
									{/if}
								</div>
							</div>

							<!-- Contenido -->
							<div class="p-4">
								<h3 class="font-bold text-foreground mb-2 line-clamp-2 group-hover:text-accent-600 transition-colors">
									{book.title}
								</h3>
								
								{#if book.author}
									<p class="text-sm text-muted-foreground mb-2 line-clamp-1">
										{book.author.name}
									</p>
								{/if}

								{#if book.firstPublishYear}
									<p class="text-xs text-muted-foreground">
										{book.firstPublishYear}
									</p>
								{/if}
							</div>
						</div>
					{/each}
				</div>
			{:else}
				<!-- Vista Lista -->
				<div class="space-y-4">
					{#each filteredBooks as book, i}
						<div 
							class="group glass rounded-2xl border border-white/20 hover:border-white/40 
								p-6 transition-all duration-300 hover:scale-[1.02] cursor-pointer"
							transition:fly={{ x: -50, duration: 600, delay: Math.min(i * 30, 300), easing: quintOut }}
						>
							<div class="flex items-center space-x-6">
								<!-- Miniatura -->
								<div class="flex-shrink-0 w-16 h-20 bg-gradient-to-br from-accent-100 to-accent-200 dark:from-accent-900 dark:to-accent-800 rounded-lg overflow-hidden">
									{#if book.coverId}
										<img 
											src="https://covers.openlibrary.org/b/id/{book.coverId}-S.jpg" 
											alt="Portada de {book.title}"
											class="w-full h-full object-cover"
										/>
									{:else}
										<div class="w-full h-full flex items-center justify-center">
											<BookOpen class="w-6 h-6 text-accent-500/50" />
										</div>
									{/if}
								</div>

								<!-- Información principal -->
								<div class="flex-1 min-w-0">
									<h3 class="text-lg font-bold text-foreground group-hover:text-accent-600 transition-colors truncate">
										{book.title}
									</h3>
									
									{#if book.author}
										<p class="text-muted-foreground mb-1">
											{book.author.name}
										</p>
									{/if}

									<div class="flex items-center space-x-4 text-sm text-muted-foreground">
										{#if book.firstPublishYear}
											<span class="flex items-center space-x-1">
												<Calendar class="w-4 h-4" />
												<span>{book.firstPublishYear}</span>
											</span>
										{/if}
										
										{#if book.language}
											<span class="flex items-center space-x-1">
												<Globe class="w-4 h-4" />
												<span>{getLanguageFlag(book.language)} {book.language.toUpperCase()}</span>
											</span>
										{/if}
										
										{#if book.searchCount > 0}
											<span class="flex items-center space-x-1">
												<TrendingUp class="w-4 h-4" />
												<span>{book.searchCount} búsquedas</span>
											</span>
										{/if}
									</div>
								</div>

								<!-- Acciones -->
								<div class="flex-shrink-0 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
									<button class="p-2 bg-accent-500 hover:bg-accent-600 text-white rounded-lg transition-colors">
										<Heart class="w-4 h-4" />
									</button>
								</div>
							</div>
						</div>
					{/each}
				</div>
			{/if}
		{/if}
	</div>
</div> 