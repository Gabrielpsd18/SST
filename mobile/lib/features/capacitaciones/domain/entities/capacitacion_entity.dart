import 'package:intl/intl.dart';

class CapacitacionEntity {
  final int id;
  final String tema;
  final String tipo;
  final DateTime fechaProgramada;
  final double duracionHoras;
  final String estado;
  final String asistencia;
  final String capacitadorPrincipal;
  final List<String> linksVideo;
  final List<String> linksEvaluacion;

  const CapacitacionEntity({
    required this.id,
    required this.tema,
    required this.tipo,
    required this.fechaProgramada,
    required this.duracionHoras,
    required this.estado,
    required this.asistencia,
    required this.capacitadorPrincipal,
    required this.linksVideo,
    required this.linksEvaluacion,
  });

  bool get isPendiente =>
      estado.toUpperCase() == 'PROGRAMADO' ||
      estado.toUpperCase() == 'EN_CURSO' ||
      fechaProgramada.isAfter(DateTime.now());

  bool get isRealizada => !isPendiente;

  bool get hasVideos => linksVideo.isNotEmpty;
  bool get hasEvaluaciones => linksEvaluacion.isNotEmpty;

  String get tipoLabel {
    switch (tipo.toUpperCase()) {
      case 'CHARLA_5_MINUTOS':
        return 'Charla de 5 Minutos';
      case 'INDUCCION':
        return 'Inducción General';
      case 'CAPACITACION':
        return 'Capacitación SST';
      default:
        return tipo;
    }
  }

  String get fechaFormateada {
    try {
      return DateFormat('dd/MM/yyyy - hh:mm a').format(fechaProgramada);
    } catch (_) {
      return fechaProgramada.toString();
    }
  }

  factory CapacitacionEntity.fromJson(Map<String, dynamic> json) {
    DateTime parseDate(dynamic date) {
      if (date == null) return DateTime.now();
      if (date is DateTime) return date;
      try {
        return DateTime.parse(date.toString());
      } catch (_) {
        return DateTime.now();
      }
    }

    double parseDouble(dynamic val) {
      if (val == null) return 1.0;
      if (val is num) return val.toDouble();
      return double.tryParse(val.toString()) ?? 1.0;
    }

    List<String> parseStringList(dynamic list) {
      if (list == null) return [];
      if (list is List) {
        return list.map((e) => e.toString().trim()).where((e) => e.isNotEmpty).toList();
      }
      return [];
    }

    return CapacitacionEntity(
      id: json['id'] is int ? json['id'] : int.tryParse('${json['id']}') ?? 0,
      tema: json['tema'] ?? 'Sin tema especificado',
      tipo: json['tipo'] ?? 'CAPACITACION',
      fechaProgramada: parseDate(json['fechaProgramada']),
      duracionHoras: parseDouble(json['duracionHoras']),
      estado: json['estado'] ?? 'PROGRAMADO',
      asistencia: json['asistencia'] ?? 'PENDIENTE',
      capacitadorPrincipal: json['capacitadorPrincipal'] ?? 'Por asignar',
      linksVideo: parseStringList(json['linksVideo']),
      linksEvaluacion: parseStringList(json['linksEvaluacion']),
    );
  }
}

class PaginatedCapacitaciones {
  final List<CapacitacionEntity> items;
  final int totalElements;
  final int totalPages;
  final int currentPage;
  final bool isLast;

  const PaginatedCapacitaciones({
    required this.items,
    required this.totalElements,
    required this.totalPages,
    required this.currentPage,
    required this.isLast,
  });

  factory PaginatedCapacitaciones.fromJson(Map<String, dynamic> json) {
    final rawContent = json['content'] as List? ?? [];
    final items = rawContent
        .map((item) => CapacitacionEntity.fromJson(item as Map<String, dynamic>))
        .toList();

    return PaginatedCapacitaciones(
      items: items,
      totalElements: json['totalElements'] is int ? json['totalElements'] : 0,
      totalPages: json['totalPages'] is int ? json['totalPages'] : 1,
      currentPage: json['number'] is int ? json['number'] : 0,
      isLast: json['last'] is bool ? json['last'] : true,
    );
  }
}
