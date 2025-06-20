<script>
	import Icon from './Icon.svelte';
	import { scale, fly } from 'svelte/transition';
	import { quintOut } from 'svelte/easing';
	
	export let book;
	export let variant = 'default'; // 'default' | 'compact' | 'featured'
	export let showActions = true;
	export let clickable = true;
	export let animationDelay = 0;
	
	let isHovered = $state(false);
	let isLiked = $state(false);
	
	// Función para obtener bandera del idioma
	function getLanguageFlag(language) {
		const flags = {
			'es': '🇪🇸', 'en': '🇺🇸', 'fr': '🇫🇷', 'pt': '🇧🇷',
			'de': '🇩🇪', 'it': '🇮🇹', 'ru': '🇷🇺', 'ja': '🇯🇵'
		};
		return flags[language] || '🌍';
	}
	
	// Manejo de eventos
	function handleClick() {
		if (clickable) {
			// Dispatch custom event
			dispatch('click', book);
		}
	}
	
	function handleLike() {
		isLiked = !isLiked;
		// Dispatch like event
		dispatch('like', { book, liked: isLiked });
	}
	
	function handleShare() {
		// Dispatch share event
		dispatch('share', book);
	}
	
	import { createEventDispatcher } from 'svelte';
	const dispatch = createEventDispatcher();
</script>

<!-- Tarjeta principal -->
<div 
	class="group relative overflow-hidden transition-all duration-500 ease-out cursor-pointer
		{variant === 'featured' ? 'lg:col-span-2' : ''}
		{clickable ? 'hover:scale-105 hover:-translate-y-2' : ''}
		{variant === 'compact' ? 'flex items-center space-x-4 p-4' : 'flex flex-col'}
		glass rounded-2xl border border-white/20 hover:border-white/40 hover:shadow-2xl"
	onmouseenter={() => isHovered = true}
	onmouseleave={() => isHovered = false}
	onclick={handleClick}
	transition:fly={{ y: 50, duration: 600, delay: animationDelay, easing: quintOut }}
>
	<!-- Gradient overlay dinámico -->
	<div class="absolute inset-0 bg-gradient-to-br from-accent-500/5 via-purple-500/5 to-blue-500/5 
		opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>
	
	<!-- Imagen del libro -->
	<div class="relative {variant === 'compact' ? 'flex-shrink-0 w-16 h-20' : 'h-64'} 
		bg-gradient-to-br from-accent-100 to-accent-200 dark:from-accent-900 dark:to-accent-800
		{variant === 'compact' ? 'rounded-lg' : 'rounded-t-2xl'} overflow-hidden">
		
		{#if book.coverId}
			<img 
				src="https://covers.openlibrary.org/b/id/{book.coverId}-{variant === 'compact' ? 'S' : 'M'}.jpg" 
				alt="Portada de {book.title}"
				class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
			/>
		{:else}
			<div class="w-full h-full flex items-center justify-center">
				<Icon name="BookOpen" class="{variant === 'compact' ? 'w-6 h-6' : 'w-16 h-16'} text-accent-500/50" />
			</div>
		{/if}
		
		<!-- Badges flotantes -->
		{#if variant !== 'compact'}
			<div class="absolute top-3 right-3 flex flex-col space-y-2">
				{#if book.searchCount > 0}
					<div class="px-2 py-1 bg-black/60 backdrop-blur-sm rounded-full text-xs text-white 
						flex items-center space-x-1 transform transition-transform duration-300
						{isHovered ? 'scale-110' : 'scale-100'}" 
						transition:scale={{ duration: 200, delay: 100 }}>
						<Icon name="Eye" class="w-3 h-3" />
						<span>{book.searchCount}</span>
					</div>
				{/if}
				
				{#if book.language}
					<div class="px-2 py-1 bg-black/60 backdrop-blur-sm rounded-full text-xs text-white
						transform transition-transform duration-300
						{isHovered ? 'scale-110' : 'scale-100'}"
						transition:scale={{ duration: 200, delay: 150 }}>
						{getLanguageFlag(book.language)}
					</div>
				{/if}
			</div>
		{/if}
		
		<!-- Acciones rápidas en hover -->
		{#if showActions && variant !== 'compact'}
			<div class="absolute inset-0 bg-black/40 flex items-center justify-center space-x-3
				opacity-0 group-hover:opacity-100 transition-opacity duration-300">
				<button 
					onclick|stopPropagation={handleLike}
					class="p-2 bg-white/20 backdrop-blur-sm rounded-full hover:bg-white/30 
						transition-all duration-200 hover:scale-110"
					transition:scale={{ duration: 200, delay: 200 }}
				>
					<Icon name="Heart" class="w-4 h-4 text-white {isLiked ? 'fill-red-500 text-red-500' : ''}" />
				</button>
				
				<button 
					onclick|stopPropagation={handleShare}
					class="p-2 bg-white/20 backdrop-blur-sm rounded-full hover:bg-white/30 
						transition-all duration-200 hover:scale-110"
					transition:scale={{ duration: 200, delay: 250 }}
				>
					<Icon name="Share" class="w-4 h-4 text-white" />
				</button>
			</div>
		{/if}
	</div>
	
	<!-- Contenido del libro -->
	<div class="{variant === 'compact' ? 'flex-1 min-w-0' : 'p-6'} relative z-10">
		<!-- Título -->
		<h3 class="font-bold text-foreground mb-2 group-hover:text-accent-600 transition-colors duration-300
			{variant === 'compact' ? 'text-base line-clamp-1' : 'text-xl line-clamp-2'}
			{variant === 'featured' ? 'text-2xl' : ''}">
			{book.title}
		</h3>
		
		<!-- Autor -->
		{#if book.author}
			<div class="flex items-center space-x-2 text-muted-foreground mb-3
				{variant === 'compact' ? 'text-sm' : ''}">
				<Icon name="User" class="w-4 h-4 flex-shrink-0" />
				<span class="truncate">{book.author.name}</span>
			</div>
		{/if}
		
		<!-- Metadata -->
		<div class="flex items-center justify-between text-sm text-muted-foreground mb-4">
			{#if book.firstPublishYear}
				<div class="flex items-center space-x-1">
					<Icon name="Calendar" class="w-4 h-4" />
					<span>{book.firstPublishYear}</span>
				</div>
			{/if}
			
			{#if book.language && variant !== 'compact'}
				<div class="flex items-center space-x-1">
					<Icon name="Globe" class="w-4 h-4" />
					<span class="uppercase">{book.language}</span>
				</div>
			{/if}
		</div>
		
		<!-- Estadísticas adicionales para variante featured -->
		{#if variant === 'featured' && book.searchCount > 0}
			<div class="flex items-center justify-between p-3 bg-white/5 rounded-lg mb-4">
				<div class="flex items-center space-x-2">
					<Icon name="TrendingUp" class="w-5 h-5 text-green-500" />
					<span class="text-sm font-medium text-foreground">Popular</span>
				</div>
				<div class="text-sm text-muted-foreground">
					{book.searchCount} búsquedas
				</div>
			</div>
		{/if}
		
		<!-- Botón de acción para variante no compacta -->
		{#if variant !== 'compact' && showActions}
			<button class="w-full px-4 py-3 bg-gradient-to-r from-accent-500 to-accent-600 
				hover:from-accent-600 hover:to-accent-700 text-white rounded-xl font-medium 
				transition-all duration-300 transform hover:scale-105 
				opacity-0 group-hover:opacity-100 translate-y-2 group-hover:translate-y-0
				shadow-lg hover:shadow-xl">
				Ver Detalles
			</button>
		{/if}
	</div>
	
	<!-- Efecto de brillo en hover -->
	<div class="absolute inset-0 pointer-events-none">
		<div class="absolute inset-0 bg-gradient-to-r from-transparent via-white/10 to-transparent 
			-translate-x-full group-hover:translate-x-full transition-transform duration-1000 ease-out
			transform -skew-x-12"></div>
	</div>
</div>

<style>
	.line-clamp-1 {
		display: -webkit-box;
		-webkit-line-clamp: 1;
		-webkit-box-orient: vertical;
		overflow: hidden;
	}
	
	.line-clamp-2 {
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		overflow: hidden;
	}
</style> 