import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/capacitaciones_provider.dart';
import '../../domain/entities/capacitacion_entity.dart';
import 'capacitacion_detalle_screen.dart';

class CapacitacionesScreen extends StatefulWidget {
  const CapacitacionesScreen({super.key});

  @override
  State<CapacitacionesScreen> createState() => _CapacitacionesScreenState();
}

class _CapacitacionesScreenState extends State<CapacitacionesScreen> {
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    _scrollController.addListener(_onScroll);
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<CapacitacionesProvider>().fetchCapacitaciones(refresh: true);
    });
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  void _onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      context.read<CapacitacionesProvider>().loadMore();
    }
  }

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<CapacitacionesProvider>();

    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        title: const Text(
          'Capacitaciones SST',
          style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
        ),
        backgroundColor: const Color(0xFF0F172A),
        foregroundColor: Colors.white,
      ),
      body: Column(
        children: [
          // Selector de Filtro (Segmented tabs)
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
            color: Colors.white,
            child: Row(
              children: [
                _FilterTab(
                  label: 'Todas',
                  isActive: provider.filtro == 'TODOS',
                  onTap: () => provider.setFilter('TODOS'),
                ),
                const SizedBox(width: 8),
                _FilterTab(
                  label: 'Pendientes',
                  isActive: provider.filtro == 'PENDIENTES',
                  badgeColor: const Color(0xFFFEF3C7),
                  badgeTextColor: const Color(0xFFB45309),
                  onTap: () => provider.setFilter('PENDIENTES'),
                ),
                const SizedBox(width: 8),
                _FilterTab(
                  label: 'Realizadas',
                  isActive: provider.filtro == 'REALIZADAS',
                  badgeColor: const Color(0xFFDCFCE7),
                  badgeTextColor: const Color(0xFF15803D),
                  onTap: () => provider.setFilter('REALIZADAS'),
                ),
              ],
            ),
          ),
          const Divider(height: 1, color: Color(0xFFE2E8F0)),

          // Contenido Principal
          Expanded(
            child: Builder(
              builder: (context) {
                if (provider.isLoading && provider.items.isEmpty) {
                  return const Center(
                    child: CircularProgressIndicator(
                      valueColor: AlwaysStoppedAnimation<Color>(Color(0xFF0F172A)),
                    ),
                  );
                }

                if (provider.hasError && provider.items.isEmpty) {
                  return Center(
                    child: Padding(
                      padding: const EdgeInsets.all(24),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          const Icon(Icons.error_outline, size: 48, color: Colors.redAccent),
                          const SizedBox(height: 12),
                          Text(
                            provider.errorMessage ?? 'Ocurrió un error al cargar',
                            textAlign: TextAlign.center,
                            style: const TextStyle(fontSize: 14, color: Color(0xFF334155)),
                          ),
                          const SizedBox(height: 16),
                          ElevatedButton.icon(
                            onPressed: () => provider.fetchCapacitaciones(refresh: true),
                            icon: const Icon(Icons.refresh),
                            label: const Text('Reintentar'),
                            style: ElevatedButton.styleFrom(
                              backgroundColor: const Color(0xFF0F172A),
                              foregroundColor: Colors.white,
                            ),
                          ),
                        ],
                      ),
                    ),
                  );
                }

                if (provider.items.isEmpty) {
                  return Center(
                    child: Padding(
                      padding: const EdgeInsets.all(24),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(Icons.school_outlined, size: 56, color: Colors.grey.shade400),
                          const SizedBox(height: 12),
                          Text(
                            provider.filtro == 'PENDIENTES'
                                ? 'No tienes capacitaciones pendientes por realizar.'
                                : provider.filtro == 'REALIZADAS'
                                    ? 'No hay capacitaciones en tu historial aún.'
                                    : 'No se encontraron capacitaciones registradas.',
                            textAlign: TextAlign.center,
                            style: const TextStyle(fontSize: 14, color: Color(0xFF64748B)),
                          ),
                          const SizedBox(height: 12),
                          TextButton.icon(
                            onPressed: () => provider.fetchCapacitaciones(refresh: true),
                            icon: const Icon(Icons.refresh, size: 18),
                            label: const Text('Actualizar'),
                          ),
                        ],
                      ),
                    ),
                  );
                }

                return RefreshIndicator(
                  onRefresh: () => provider.fetchCapacitaciones(refresh: true),
                  color: const Color(0xFF0F172A),
                  child: ListView.separated(
                    controller: _scrollController,
                    padding: const EdgeInsets.all(16),
                    itemCount: provider.items.length + (provider.isLoadingMore ? 1 : 0),
                    separatorBuilder: (context, index) => const SizedBox(height: 12),
                    itemBuilder: (context, index) {
                      if (index == provider.items.length) {
                        return const Padding(
                          padding: EdgeInsets.symmetric(vertical: 16),
                          child: Center(
                            child: SizedBox(
                              width: 24,
                              height: 24,
                              child: CircularProgressIndicator(strokeWidth: 2.5),
                            ),
                          ),
                        );
                      }

                      final capacitacion = provider.items[index];
                      return _CapacitacionCard(
                        capacitacion: capacitacion,
                        onTap: () {
                          Navigator.of(context).push(
                            MaterialPageRoute(
                              builder: (_) => CapacitacionDetalleScreen(capacitacion: capacitacion),
                            ),
                          );
                        },
                      );
                    },
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}

class _FilterTab extends StatelessWidget {
  final String label;
  final bool isActive;
  final Color? badgeColor;
  final Color? badgeTextColor;
  final VoidCallback onTap;

  const _FilterTab({
    required this.label,
    required this.isActive,
    this.badgeColor,
    this.badgeTextColor,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(8),
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 8),
          decoration: BoxDecoration(
            color: isActive ? const Color(0xFF0F172A) : const Color(0xFFF1F5F9),
            borderRadius: BorderRadius.circular(8),
          ),
          alignment: Alignment.center,
          child: Text(
            label,
            style: TextStyle(
              fontSize: 12,
              fontWeight: FontWeight.bold,
              color: isActive ? Colors.white : const Color(0xFF475569),
            ),
          ),
        ),
      ),
    );
  }
}

class _CapacitacionCard extends StatelessWidget {
  final CapacitacionEntity capacitacion;
  final VoidCallback onTap;

  const _CapacitacionCard({
    required this.capacitacion,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final isPendiente = capacitacion.isPendiente;

    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(12),
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: const Color(0xFFE2E8F0)),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withValues(alpha: 0.02),
              blurRadius: 6,
              offset: const Offset(0, 2),
            ),
          ],
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Cabecera: Tipo + Estado
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                  decoration: BoxDecoration(
                    color: const Color(0xFFEFF6FF),
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: Text(
                    capacitacion.tipoLabel,
                    style: const TextStyle(
                      color: Color(0xFF2563EB),
                      fontSize: 11,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                  decoration: BoxDecoration(
                    color: isPendiente ? const Color(0xFFFEF3C7) : const Color(0xFFDCFCE7),
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: Text(
                    isPendiente ? 'PENDIENTE' : 'REALIZADA',
                    style: TextStyle(
                      color: isPendiente ? const Color(0xFFB45309) : const Color(0xFF15803D),
                      fontSize: 10,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 10),

            // Tema / Título
            Text(
              capacitacion.tema,
              style: const TextStyle(
                fontSize: 15,
                fontWeight: FontWeight.bold,
                color: Color(0xFF0F172A),
              ),
              maxLines: 2,
              overflow: TextOverflow.ellipsis,
            ),
            const SizedBox(height: 10),

            // Fecha y duración
            Row(
              children: [
                const Icon(Icons.calendar_today_outlined, size: 14, color: Color(0xFF64748B)),
                const SizedBox(width: 4),
                Text(
                  capacitacion.fechaFormateada,
                  style: const TextStyle(fontSize: 12, color: Color(0xFF64748B)),
                ),
                const SizedBox(width: 12),
                const Icon(Icons.schedule, size: 14, color: Color(0xFF64748B)),
                const SizedBox(width: 4),
                Text(
                  '${capacitacion.duracionHoras}h',
                  style: const TextStyle(fontSize: 12, color: Color(0xFF64748B)),
                ),
              ],
            ),
            const SizedBox(height: 12),
            const Divider(color: Color(0xFFF1F5F9), height: 1),
            const SizedBox(height: 10),

            // Footer con badges de Videos / Formularios y botón Ver detalle
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Row(
                  children: [
                    if (capacitacion.hasVideos) ...[
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                        decoration: BoxDecoration(
                          color: const Color(0xFFFEE2E2),
                          borderRadius: BorderRadius.circular(4),
                        ),
                        child: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            const Icon(Icons.play_circle_outline, color: Color(0xFFDC2626), size: 12),
                            const SizedBox(width: 4),
                            Text(
                              '${capacitacion.linksVideo.length} video(s)',
                              style: const TextStyle(fontSize: 10, color: Color(0xFFDC2626), fontWeight: FontWeight.w600),
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(width: 6),
                    ],
                    if (capacitacion.hasEvaluaciones) ...[
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                        decoration: BoxDecoration(
                          color: const Color(0xFFDBEAFE),
                          borderRadius: BorderRadius.circular(4),
                        ),
                        child: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            const Icon(Icons.description_outlined, color: Color(0xFF2563EB), size: 12),
                            const SizedBox(width: 4),
                            Text(
                              '${capacitacion.linksEvaluacion.length} form(s)',
                              style: const TextStyle(fontSize: 10, color: Color(0xFF2563EB), fontWeight: FontWeight.w600),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ],
                ),
                const Row(
                  children: [
                    Text(
                      'Ver detalle',
                      style: TextStyle(fontSize: 12, color: Color(0xFF0284C7), fontWeight: FontWeight.bold),
                    ),
                    Icon(Icons.chevron_right, size: 16, color: Color(0xFF0284C7)),
                  ],
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
