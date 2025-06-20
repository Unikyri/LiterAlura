<script>
  import { icons } from 'lucide';

  let {
    name,
    size = 24,
    color = 'currentColor',
    strokeWidth = 2,
    class: className = '',
    ...restProps
  } = $props();

  let svgElement = $state();

  $effect(() => {
    if (svgElement && name && icons[name]) {
      svgElement.innerHTML = icons[name].toSvg({
        width: size,
        height: size,
        color,
        'stroke-width': strokeWidth,
        ...restProps
      });
    }
  });
</script>

<span
  bind:this={svgElement}
  class="inline-flex {className}"
  style="width: {size}px; height: {size}px;"
></span> 