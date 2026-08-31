import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';
import '../../domain/entities/capacitacion_entity.dart';

class CapacitacionDetalleScreen extends StatelessWidget {
  final CapacitacionEntity capacitacion;

  const CapacitacionDetalleScreen({
    super.key,
    required this.capacitacion,
  });

  Future<void> _abrirEnlace(BuildContext context, String url) async {
    final cleanUrl = url.trim();
    if (cleanUrl.isEmpty) return;

    final uri = Uri.tryParse(cleanUrl.startsWith('http') ? cleanUrl : 'https://$cleanUrl');
    if (uri == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Enlace no válido')),
      );
      return;
    }

    try {
      final launched = await launchUrl(
        uri,
        mode: LaunchMode.externalApplication,
      );
      if (!launched && context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('No se pudo abrir el enlace: $cleanUrl')),
        );
      }
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error al abrir enlace: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final isPendiente = capacitacion.isPendiente;

    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        title: const Text('Detalle de Capacitación', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
        backgroundColor: const Color(0xFF0F172A),
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Tarjeta Principal
            Container(
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(16),
                border: Border.all(color: const Color(0xFFE2E8F0)),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withValues(alpha: 0.03),
                    blurRadius: 10,
                    offset: const Offset(0, 4),
                  ),
                ],
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Badges de Tipo y Estado
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                        decoration: BoxDecoration(
                          color: const Color(0xFFEFF6FF),
                          borderRadius: BorderRadius.circular(6),
                          border: Border.all(color: const Color(0xFFBFDBFE)),
                        ),
                        child: Text(
                          capacitacion.tipoLabel,
                          style: const TextStyle(
                            color: Color(0xFF1D4ED8),
                            fontSize: 12,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                        decoration: BoxDecoration(
                          color: isPendiente ? const Color(0xFFFEF3C7) : const Color(0xFFDCFCE7),
                          borderRadius: BorderRadius.circular(6),
                        ),
                        child: Text(
                          isPendiente ? 'PENDIENTE' : 'REALIZADA',
                          style: TextStyle(
                            color: isPendiente ? const Color(0xFFB45309) : const Color(0xFF15803D),
                            fontSize: 11,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 14),

                  // Tema / Título
                  Text(
                    capacitacion.tema,
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: Color(0xFF0F172A),
                      height: 1.3,
                    ),
                  ),
                  const SizedBox(height: 18),
                  const Divider(color: Color(0xFFF1F5F9), height: 1),
                  const SizedBox(height: 16),

                  // Detalles informativos
                  _InfoRow(
                    icon: Icons.calendar_today_outlined,
                    label: 'Fecha y Hora:',
                    value: capacitacion.fechaFormateada,
                  ),
                  const SizedBox(height: 10),
                  _InfoRow(
                    icon: Icons.access_time_outlined,
                    label: 'Duración Estimada:',
                    value: '${capacitacion.duracionHoras} hora(s)',
                  ),
                  const SizedBox(height: 10),
                  _InfoRow(
                    icon: Icons.person_outline,
                    label: 'Capacitador:',
                    value: capacitacion.capacitadorPrincipal,
                  ),
                  const SizedBox(height: 10),
                  _InfoRow(
                    icon: Icons.check_circle_outline,
                    label: 'Asistencia:',
                    value: capacitacion.asistencia,
                    valueColor: capacitacion.asistencia.toUpperCase() == 'ASISTIO'
                        ? const Color(0xFF16A34A)
                        : const Color(0xFF64748B),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),

            // SECCIÓN: VIDEOS DE CAPACITACIÓN
            const Row(
              children: [
                Icon(Icons.video_library_outlined, color: Color(0xFFDC2626), size: 22),
                SizedBox(width: 8),
                Text(
                  'Material Audiovisual y Videos',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                    color: Color(0xFF0F172A),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 10),
            if (capacitacion.linksVideo.isEmpty)
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: const Color(0xFFE2E8F0)),
                ),
                child: const Row(
                  children: [
                    Icon(Icons.info_outline, color: Color(0xFF94A3B8), size: 20),
                    SizedBox(width: 10),
                    Expanded(
                      child: Text(
                        'No hay enlaces de video registrados para esta capacitación.',
                        style: TextStyle(color: Color(0xFF64748B), fontSize: 13),
                      ),
                    ),
                  ],
                ),
              )
            else
              ...capacitacion.linksVideo.asMap().entries.map((entry) {
                final idx = entry.key + 1;
                final url = entry.value;
                return Container(
                  margin: const EdgeInsets.only(bottom: 10),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(12),
                    border: Border.all(color: const Color(0xFFE2E8F0)),
                  ),
                  child: ListTile(
                    leading: Container(
                      padding: const EdgeInsets.all(8),
                      decoration: BoxDecoration(
                        color: const Color(0xFFFEE2E2),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: const Icon(Icons.play_circle_fill, color: Color(0xFFDC2626), size: 24),
                    ),
                    title: Text(
                      'Ver Video #$idx',
                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14, color: Color(0xFF0F172A)),
                    ),
                    subtitle: Text(
                      url,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: const TextStyle(fontSize: 12, color: Color(0xFF64748B)),
                    ),
                    trailing: const Icon(Icons.open_in_new, color: Color(0xFF0284C7), size: 20),
                    onTap: () => _abrirEnlace(context, url),
                  ),
                );
              }),
            const SizedBox(height: 24),

            // SECCIÓN: FORMULARIOS DE EVALUACIÓN
            const Row(
              children: [
                Icon(Icons.description_outlined, color: Color(0xFF2563EB), size: 22),
                SizedBox(width: 8),
                Text(
                  'Evaluación y Asistencia',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                    color: Color(0xFF0F172A),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 10),
            if (capacitacion.linksEvaluacion.isEmpty)
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: const Color(0xFFE2E8F0)),
                ),
                child: const Row(
                  children: [
                    Icon(Icons.info_outline, color: Color(0xFF94A3B8), size: 20),
                    SizedBox(width: 10),
                    Expanded(
                      child: Text(
                        'No hay formularios de evaluación registrados para esta capacitación.',
                        style: TextStyle(color: Color(0xFF64748B), fontSize: 13),
                      ),
                    ),
                  ],
                ),
              )
            else
              ...capacitacion.linksEvaluacion.asMap().entries.map((entry) {
                final idx = entry.key + 1;
                final url = entry.value;
                return Container(
                  margin: const EdgeInsets.only(bottom: 10),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(12),
                    border: Border.all(color: const Color(0xFFE2E8F0)),
                  ),
                  child: ListTile(
                    leading: Container(
                      padding: const EdgeInsets.all(8),
                      decoration: BoxDecoration(
                        color: const Color(0xFFDBEAFE),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: const Icon(Icons.assignment_turned_in, color: Color(0xFF2563EB), size: 24),
                    ),
                    title: Text(
                      'Formulario de Evaluación #$idx',
                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14, color: Color(0xFF0F172A)),
                    ),
                    subtitle: Text(
                      url,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: const TextStyle(fontSize: 12, color: Color(0xFF64748B)),
                    ),
                    trailing: const Icon(Icons.open_in_new, color: Color(0xFF0284C7), size: 20),
                    onTap: () => _abrirEnlace(context, url),
                  ),
                );
              }),
          ],
        ),
      ),
    );
  }
}

class _InfoRow extends StatelessWidget {
  final IconData icon;
  final String label;
  final String value;
  final Color? valueColor;

  const _InfoRow({
    required this.icon,
    required this.label,
    required this.value,
    this.valueColor,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Icon(icon, size: 18, color: const Color(0xFF64748B)),
        const SizedBox(width: 8),
        Text(
          label,
          style: const TextStyle(fontSize: 13, color: Color(0xFF64748B), fontWeight: FontWeight.w500),
        ),
        const SizedBox(width: 6),
        Expanded(
          child: Text(
            value,
            style: TextStyle(
              fontSize: 13,
              color: valueColor ?? const Color(0xFF0F172A),
              fontWeight: FontWeight.w600,
            ),
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
          ),
        ),
      ],
    );
  }
}
