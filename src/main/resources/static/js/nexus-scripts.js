(() => {
    'use strict';

    const csrfToken = document.querySelector("meta[name='_csrf']")?.content;
    const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.content || 'X-CSRF-TOKEN';

    function withCsrf(headers = {}) {
        if (csrfToken) headers[csrfHeader] = csrfToken;
        return headers;
    }

    function getTrialDaysRemaining() {
        const status = localStorage.getItem('accountStatus');
        if (status === 'ASSINANTE_PAGO') return Number.POSITIVE_INFINITY;

        let startDate = localStorage.getItem('trialStartDate');
        const storedDays = Number(localStorage.getItem('trialDaysRemaining'));
        if (!startDate && Number.isFinite(storedDays)) {
            const inferredStart = new Date(Date.now() - Math.max(0, 30 - storedDays) * 86400000);
            startDate = inferredStart.toISOString();
            localStorage.setItem('trialStartDate', startDate);
        }
        if (!startDate) return null;

        const expiration = new Date(startDate);
        expiration.setDate(expiration.getDate() + 30);
        return Math.ceil((expiration.getTime() - Date.now()) / 86400000);
    }

    function atualizarAcessoTrial() {
        const banner = document.getElementById('bannerAlertaTrial');
        const lock = document.getElementById('screenLockSaas');
        if (!banner || !lock) return;

        const status = localStorage.getItem('accountStatus');
        const days = getTrialDaysRemaining();
        const expired = status === 'EXPIRADO' || (days !== null && days <= 0);

        banner.classList.toggle('is-visible', !expired && days !== null && days > 0 && days <= 5);
        if (!expired && days !== null && days > 0) {
            document.getElementById('trialDiasRestantes').textContent = `${days} ${days === 1 ? 'dia' : 'dias'}`;
            localStorage.setItem('trialDaysRemaining', String(days));
        }
        if (expired) lock.classList.add('is-visible');
    }

    window.abrirCheckoutSaas = function abrirCheckoutSaas() {
        document.getElementById('screenLockSaas')?.classList.add('is-visible');
    };

    window.trocarCheckout = function trocarCheckout(pane, button) {
        document.querySelectorAll('.checkout-pane').forEach(element => element.classList.toggle('active', element.id === `checkoutPane${pane.charAt(0).toUpperCase()}${pane.slice(1)}`));
        document.querySelectorAll('.checkout-tabs button').forEach(element => element.classList.remove('active'));
        button?.classList.add('active');
    };

    window.copiarChavePix = async function copiarChavePix() {
        const key = document.getElementById('pixCopiaCola')?.textContent || '';
        try {
            await navigator.clipboard.writeText(key);
            window.alert('Chave PIX copiada.');
        } catch {
            window.alert(`Chave PIX: ${key}`);
        }
    };

    window.confirmarPagamentoSaas = function confirmarPagamentoSaas() {
        localStorage.setItem('accountStatus', 'ASSINANTE_PAGO');
        localStorage.removeItem('trialDaysRemaining');
        document.getElementById('bannerAlertaTrial')?.classList.remove('is-visible');
        const lock = document.getElementById('screenLockSaas');
        lock?.classList.add('is-leaving');
        setTimeout(() => lock?.classList.remove('is-visible', 'is-leaving'), 450);
        const badge = document.getElementById('trialStatusBadge');
        if (badge) badge.textContent = 'Plano Corporativo: Ativo';
    };

    function moneyValue(id) {
        const raw = document.getElementById(id)?.value || '';
        const normalized = String(raw).replace(',', '.');
        const value = Number.parseFloat(normalized);
        return Number.isFinite(value) ? value : 0;
    }

    window.calcularValorTotalOS = function calcularValorTotalOS() {
        const total = moneyValue('modalLeadServiceValue') + moneyValue('modalLeadPartsValue');
        const totalField = document.getElementById('modalLeadValue');
        if (totalField) totalField.value = total.toFixed(2);
        return total;
    };

    window.salvarCatalogoEmLote = async function salvarCatalogoEmLote(rows) {
        if (!Array.isArray(rows) || rows.length === 0) return [];
        const responses = [];
        for (const row of rows) {
            const response = await fetch('/api/service-catalog', {
                method: 'POST',
                headers: withCsrf({ 'Content-Type': 'application/json' }),
                body: JSON.stringify(row)
            });
            if (!response.ok) throw new Error(`Falha ao salvar o serviço: ${response.status}`);
            responses.push(await response.json());
        }
        return responses;
    };

    window.consultarPlaca = async function consultarPlaca(plate) {
        const normalized = String(plate || '').replace(/[^A-Za-z0-9]/g, '').toUpperCase();
        if (!normalized) return null;
        const localMatch = [...document.querySelectorAll('[data-veiculo-placa]')]
            .find(element => (element.dataset.veiculoPlaca || '').replace(/[^A-Za-z0-9]/g, '').toUpperCase() === normalized);
        return localMatch ? {
            source: 'local',
            element: localMatch,
            chassis: localMatch.dataset.veiculoChassis || '',
            km: localMatch.dataset.veiculoKm || ''
        } : { source: 'not-found', plate: normalized };
    };

    window.buscarDadosVeiculo = async function buscarDadosVeiculo() {
        const plate = document.getElementById('modalLeadVehiclePlate')?.value;
        const result = await window.consultarPlaca(plate);
        if (!result) return;

        if (result.source === 'local') {
            const chassis = document.getElementById('modalLeadVehicleChassis');
            const km = document.getElementById('modalLeadVehicleKm');
            if (chassis && result.chassis) chassis.value = result.chassis;
            if (km && result.km) km.value = result.km;
            return;
        }

        window.alert('Nenhum veículo encontrado no histórico local para esta placa.');
    };

    function alternarVisaoOS(view) {
        const sheet = document.getElementById('visaoPlanilhaOS');
        const board = document.getElementById('kanbanBoard');
        if (!sheet || !board) return;

        const mostrarPlanilha = view === 'planilha';
        sheet.style.display = mostrarPlanilha ? 'block' : 'none';
        board.style.display = mostrarPlanilha ? 'none' : 'block';

        document.querySelectorAll('[data-id="kanban"], [data-id="ordens-servico"]').forEach(link => {
            const active = mostrarPlanilha ? link.dataset.id === 'ordens-servico' : link.dataset.id === 'kanban';
            link.classList.toggle('active', active);
        });
    }

    window.mostrarVisaoOS = function mostrarVisaoOS(view = 'planilha') {
        alternarVisaoOS(view);
    };

    window.mostrarVisaoKanban = function mostrarVisaoKanban() {
        alternarVisaoOS('kanban');
    };

    let osPecas = [];

    window.abrirSincronizacaoZap = function abrirSincronizacaoZap() {
        bootstrap.Modal.getOrCreateInstance(document.getElementById('modalSincronizacaoZap')).show();
    };

    window.ativarSincronizacaoZap = function ativarSincronizacaoZap() {
        localStorage.setItem('dt_nexus_zap_connected', 'true');
        const state = document.getElementById('zapConnectionState');
        if (state) {
            state.textContent = 'Conectado • conversas sincronizadas';
            state.style.color = '#22c55e';
        }
        bootstrap.Modal.getOrCreateInstance(document.getElementById('modalSincronizacaoZap')).hide();
    };

    window.abrirPainelOS = function abrirPainelOS(button) {
        const origin = button?.closest('.card-lead');
        const name = origin?.dataset.nome || 'Nova O.S.';
        const vehicle = origin?.dataset.veiculoModelo || 'Veículo não selecionado';
        const value = Number(origin?.dataset.valor || 0);
        document.getElementById('osContactId').value = origin?.dataset.id || '';
        document.getElementById('osClienteResumo').textContent = name;
        document.getElementById('osVeiculoResumo').textContent = vehicle;
        document.getElementById('osTotalResumo').textContent = value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
        document.getElementById('osNumero').textContent = origin?.dataset.id ? `OS-${origin.dataset.id}` : 'Nova';
        osPecas = [];
        document.getElementById('osPecasLista').innerHTML = '';
        document.querySelectorAll('.os-pane').forEach(pane => pane.classList.toggle('active', pane.id === 'osPaneExecucao'));
        document.querySelectorAll('.os-tabs button').forEach((tab, index) => tab.classList.toggle('active', index === 0));
        bootstrap.Modal.getOrCreateInstance(document.getElementById('modalPainelOS')).show();
    };

    window.trocarAbaOS = function trocarAbaOS(name, button) {
        document.querySelectorAll('.os-pane').forEach(pane => pane.classList.toggle('active', pane.id === `osPane${name.charAt(0).toUpperCase()}${name.slice(1)}`));
        document.querySelectorAll('.os-tabs button').forEach(tab => tab.classList.remove('active'));
        button?.classList.add('active');
    };

    window.simularLeituraPeca = function simularLeituraPeca() {
        const code = document.getElementById('osPecaCodigo');
        if (code) code.value = `PEC-${Date.now().toString().slice(-6)}`;
        document.getElementById('osPecaDescricao').focus();
    };

    window.adicionarPecaOS = function adicionarPecaOS() {
        const code = document.getElementById('osPecaCodigo').value.trim();
        const description = document.getElementById('osPecaDescricao').value.trim();
        const quantity = Number(document.getElementById('osPecaQuantidade').value || 0);
        const cost = Number(document.getElementById('osPecaCusto').value || 0);
        const sale = Number(document.getElementById('osPecaVenda').value || 0);
        if (!code || !description || quantity <= 0) return;
        osPecas.push({ code, description, quantity, cost, sale, stock: 'Reservado para O.S.' });
        document.getElementById('osPecasLista').innerHTML = osPecas.map(piece => `
            <tr><td>${piece.code}</td><td>${piece.description}</td><td>${piece.quantity}</td>
            <td>${piece.cost.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</td>
            <td>${piece.sale.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</td><td>${piece.stock}</td></tr>
        `).join('');
    };

    window.consultarHistoricoOS = function consultarHistoricoOS() {
        const term = document.getElementById('historicoBusca').value.trim().toLowerCase();
        const result = document.getElementById('historicoResultado');
        const matches = [...document.querySelectorAll('.card-lead')].filter(card =>
            [card.dataset.nome, card.dataset.veiculoModelo, card.dataset.veiculoPlaca]
                .some(value => (value || '').toLowerCase().includes(term))
        );
        result.innerHTML = matches.length
            ? matches.map(card => `<div class="border-bottom border-secondary border-opacity-25 py-2"><strong>${card.dataset.nome}</strong><span class="d-block text-secondary small">${card.dataset.veiculoModelo || 'Veículo'} • ${card.dataset.veiculoPlaca || 'Sem placa'} • O.S. ${card.dataset.id}</span></div>`).join('')
            : 'Nenhum registro local encontrado para a pesquisa.';
    };

    window.emitirOS = function emitirOS() {
        const osNumber = document.getElementById('osNumero').textContent;
        const inspection = {
            vehicleInspectionExternal: `Carroceria: ${document.getElementById('checkCarroceria').value}; Itens: ${document.getElementById('checkItens').value}`,
            vehicleInspectionInternal: `Painel: ${document.getElementById('checkPainel').value}; Observações: ${document.getElementById('checkObservacoes').value}`,
            vehicleFluidStatus: document.getElementById('checkOleo').value,
            vehicleFuelLevel: document.getElementById('checkCombustivel').value,
            vehicleSignatureAccepted: document.getElementById('checkAceite').checked
        };
        const osData = {
            mechanic: document.getElementById('osMecanico').value,
            service: document.getElementById('osServico').value,
            notes: document.getElementById('osObservacoes').value,
            parts: osPecas,
            checklist: inspection,
            invoice: document.getElementById('osNotaStatus').value,
            protocol: document.getElementById('osNotaProtocolo').value,
            emittedAt: new Date().toISOString()
        };
        localStorage.setItem(`dt_nexus_os_${osNumber}`, JSON.stringify(osData));

        const contactId = document.getElementById('osContactId').value;
        if (contactId) {
            fetch(`/api/contacts/${contactId}/status`, {
                method: 'PATCH',
                headers: withCsrf({ 'Content-Type': 'application/json' }),
                body: JSON.stringify(inspection)
            }).catch(() => console.warn('Checklist salvo apenas no histórico local.'));
        }
        window.alert(`${osNumber} emitida e checklist registrado.`);
    };

    window.salvarPlanilhaOSInBatch = async function salvarPlanilhaOSInBatch() {
        const token = localStorage.getItem('accessToken');
        const rows = [...document.querySelectorAll('#planilhaOSTable tbody tr')].map(row => {
            const value = field => row.querySelector(`[data-field="${field}"]`)?.value?.trim() || '';
            const numericValue = field => {
                const raw = value(field);
                return raw === '' ? null : Number(raw);
            };
            return {
                id: value('id'),
                cliente: value('cliente'),
                veiculo: value('veiculo'),
                placa: value('placa'),
                chassi: value('chassi'),
                km: numericValue('km'),
                servico: value('servico'),
                statusColumn: value('status'),
                serviceValue: numericValue('serviceValue'),
                partsValue: numericValue('partsValue'),
                valorTotal: numericValue('valorTotal')
            };
        });
        await Promise.all(rows.filter(row => row.id).map(row => fetch(`/api/contacts/${row.id}/status`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}) },
            body: JSON.stringify({
                name: row.cliente,
                vehicleModel: row.veiculo,
                vehiclePlate: row.placa,
                vehicleChassis: row.chassi,
                vehicleKm: row.km,
                serviceInterest: row.servico,
                statusColumn: row.statusColumn,
                serviceValue: row.serviceValue,
                partsValue: row.partsValue,
                value: row.valorTotal
            })
        }).then(response => {
            if (!response.ok) throw new Error(`Falha ao atualizar O.S. ${row.id}`);
            return response.json();
        })));
        return rows;
    };

    window.abrirModalPagamento = function abrirModalPagamento() {
        const modal = document.getElementById('modalPagamento');
        if (modal && window.bootstrap) bootstrap.Modal.getOrCreateInstance(modal).show();
    };

    window.simularPagamento = function simularPagamento(method) {
        window.confirmarPagamentoSaas();
        const badge = document.getElementById('trialStatusBadge');
        const result = document.getElementById('pagamentoResultado');
        if (badge) badge.textContent = 'Plano Corporativo: Ativo';
        if (result) result.innerHTML = `<span class="text-success"><i class="bi bi-check-circle me-1"></i>Pagamento ${method} confirmado em modo de demonstração.</span>`;
    };

    document.addEventListener('DOMContentLoaded', () => {
        atualizarAcessoTrial();
        if (localStorage.getItem('dt_nexus_zap_connected') === 'true') {
            const state = document.getElementById('zapConnectionState');
            if (state) {
                state.textContent = 'Conectado • conversas sincronizadas';
                state.style.color = '#22c55e';
            }
        }
        const serviceValue = document.getElementById('modalLeadServiceValue');
        const partsValue = document.getElementById('modalLeadPartsValue');
        const leadForm = document.getElementById('formLeadModal');
        serviceValue?.addEventListener('input', window.calcularValorTotalOS);
        partsValue?.addEventListener('input', window.calcularValorTotalOS);
        leadForm?.addEventListener('submit', window.calcularValorTotalOS);

        document.querySelectorAll('#planilhaOSTable tbody tr').forEach(row => {
            const service = row.querySelector('[data-field="servico"]');
            const status = row.querySelector('[data-field="status"]');
            if (service) service.value = row.dataset.servico || '';
            if (status) status.value = row.dataset.status || status.value;
        });
        const badge = document.getElementById('trialStatusBadge');
        const status = localStorage.getItem('accountStatus');
        const days = localStorage.getItem('trialDaysRemaining');
        if (badge && status === 'ASSINANTE_PAGO') badge.textContent = 'Plano Corporativo: Ativo';
        else if (badge && days !== null) badge.textContent = `Teste Grátis: ${days} Dias Restantes`;
    });
})();
