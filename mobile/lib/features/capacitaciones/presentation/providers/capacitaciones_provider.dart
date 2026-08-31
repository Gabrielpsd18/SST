import 'package:flutter/material.dart';
import '../../domain/entities/capacitacion_entity.dart';
import '../../domain/repositories/capacitaciones_repository.dart';
import '../../domain/usecases/get_capacitaciones_usecase.dart';
import '../../data/repositories/capacitaciones_repository_impl.dart';

class CapacitacionesProvider extends ChangeNotifier {
  final GetCapacitacionesUseCase _getCapacitacionesUseCase;

  List<CapacitacionEntity> _items = [];
  String _filtro = 'TODOS'; // 'TODOS', 'PENDIENTES', 'REALIZADAS'
  int _currentPage = 0;
  int _totalPages = 1;
  int _totalElements = 0;
  bool _isLastPage = false;
  bool _isLoading = false;
  bool _isLoadingMore = false;
  String? _errorMessage;

  CapacitacionesProvider({CapacitacionesRepository? repository})
      : _getCapacitacionesUseCase =
            GetCapacitacionesUseCase(repository ?? CapacitacionesRepositoryImpl());

  List<CapacitacionEntity> get items => _items;
  String get filtro => _filtro;
  int get currentPage => _currentPage;
  int get totalPages => _totalPages;
  int get totalElements => _totalElements;
  bool get isLastPage => _isLastPage;
  bool get isLoading => _isLoading;
  bool get isLoadingMore => _isLoadingMore;
  String? get errorMessage => _errorMessage;
  bool get hasError => _errorMessage != null;

  // Carga inicial o recarga (Pull to Refresh)
  Future<void> fetchCapacitaciones({bool refresh = false}) async {
    if (refresh) {
      _currentPage = 0;
      _isLastPage = false;
      _items = [];
    }

    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      final result = await _getCapacitacionesUseCase.execute(
        filtro: _filtro,
        page: _currentPage,
        size: 10,
      );

      _items = result.items;
      _totalPages = result.totalPages;
      _totalElements = result.totalElements;
      _isLastPage = result.isLast;
      _errorMessage = null;
    } catch (e) {
      _errorMessage = e.toString().replaceAll('Exception: ', '');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Cargar siguiente página al hacer scroll infinito
  Future<void> loadMore() async {
    if (_isLastPage || _isLoading || _isLoadingMore) return;

    _isLoadingMore = true;
    notifyListeners();

    try {
      final nextPage = _currentPage + 1;
      final result = await _getCapacitacionesUseCase.execute(
        filtro: _filtro,
        page: nextPage,
        size: 10,
      );

      _currentPage = nextPage;
      _items.addAll(result.items);
      _totalPages = result.totalPages;
      _totalElements = result.totalElements;
      _isLastPage = result.isLast;
    } catch (e) {
      _errorMessage = 'Error al cargar más capacitaciones: ${e.toString().replaceAll('Exception: ', '')}';
    } finally {
      _isLoadingMore = false;
      notifyListeners();
    }
  }

  // Cambiar pestaña de filtro ('TODOS', 'PENDIENTES', 'REALIZADAS')
  void setFilter(String nuevoFiltro) {
    if (_filtro == nuevoFiltro) return;
    _filtro = nuevoFiltro;
    fetchCapacitaciones(refresh: true);
  }
}
