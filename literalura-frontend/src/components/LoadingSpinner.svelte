<script>
	import Icon from './Icon.svelte';
	
	export let size = 'md'; // 'sm' | 'md' | 'lg'
	export let message = 'Cargando...';
	export let showMessage = true;
	
	const sizeClasses = {
		sm: 'w-8 h-8',
		md: 'w-12 h-12', 
		lg: 'w-16 h-16'
	};
	
	const iconSizes = {
		sm: 'w-4 h-4',
		md: 'w-6 h-6',
		lg: 'w-8 h-8'
	};
</script>

<div class="flex flex-col items-center justify-center space-y-4">
	<!-- Spinner principal -->
	<div class="relative {sizeClasses[size]}">
		<!-- Anillo externo giratorio -->
		<div class="absolute inset-0 rounded-full border-4 border-gradient-to-r from-blue-500 via-purple-500 to-pink-500 
			opacity-20 animate-spin"></div>
		
		<!-- Anillo medio -->
		<div class="absolute inset-1 rounded-full border-3 border-accent-500/40 animate-ping"></div>
		
		<!-- Centro con icono -->
		<div class="absolute inset-0 flex items-center justify-center">
			<div class="relative">
				<Icon name="BookOpen" class="{iconSizes[size]} text-accent-500 animate-pulse" />
				<Icon name="Sparkles" class="w-3 h-3 text-yellow-400 absolute -top-1 -right-1 animate-bounce" />
			</div>
		</div>
		
		<!-- Efectos de partículas -->
		<div class="absolute inset-0">
			{#each Array(6) as _, i}
				<div 
					class="absolute w-1 h-1 bg-accent-400 rounded-full animate-ping"
					style="
						top: {20 + Math.sin(i * 60 * Math.PI / 180) * 30}%;
						left: {50 + Math.cos(i * 60 * Math.PI / 180) * 30}%;
						animation-delay: {i * 200}ms;
					"
				></div>
			{/each}
		</div>
	</div>
	
	<!-- Mensaje -->
	{#if showMessage}
		<div class="text-center">
			<p class="text-sm font-medium text-foreground animate-pulse">
				{message}
			</p>
			<div class="flex justify-center space-x-1 mt-2">
				{#each Array(3) as _, i}
					<div 
						class="w-2 h-2 bg-accent-500 rounded-full animate-bounce"
						style="animation-delay: {i * 200}ms"
					></div>
				{/each}
			</div>
		</div>
	{/if}
</div>

<style>
	@keyframes gradient-spin {
		0% { 
			background: linear-gradient(0deg, #3b82f6, #8b5cf6, #ec4899);
		}
		33% { 
			background: linear-gradient(120deg, #3b82f6, #8b5cf6, #ec4899);
		}
		66% { 
			background: linear-gradient(240deg, #3b82f6, #8b5cf6, #ec4899);
		}
		100% { 
			background: linear-gradient(360deg, #3b82f6, #8b5cf6, #ec4899);
		}
	}
</style> 