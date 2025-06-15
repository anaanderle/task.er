#!/bin/bash

echo "🚀 Executando testes do projeto Tasker..."

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Função para imprimir com cores
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar se Maven está instalado
if ! command -v mvn &> /dev/null; then
    print_error "Maven não está instalado. Por favor, instale o Maven primeiro."
    exit 1
fi

# Limpar compilação anterior
print_status "Limpando compilação anterior..."
mvn clean

# Compilar o projeto
print_status "Compilando o projeto..."
if ! mvn compile test-compile; then
    print_error "Falha na compilação do projeto!"
    exit 1
fi

# Executar testes
print_status "Executando testes..."
if mvn test; then
    print_status "✅ Todos os testes passaram com sucesso!"
else
    print_error "❌ Alguns testes falharam!"
    exit 1
fi

# Gerar relatório de cobertura
print_status "Gerando relatório de cobertura..."
mvn jacoco:report

# Verificar se o relatório foi gerado
if [ -f "target/site/jacoco/index.html" ]; then
    print_status "📊 Relatório de cobertura gerado em: target/site/jacoco/index.html"
    print_status "Para visualizar, abra o arquivo em um navegador ou execute:"
    echo "    open target/site/jacoco/index.html"
else
    print_warning "Relatório de cobertura não foi gerado."
fi

# Mostrar resumo dos resultados
if [ -f "target/surefire-reports/TEST-*.xml" ]; then
    TESTS_RUN=$(grep -h "tests=" target/surefire-reports/TEST-*.xml | sed 's/.*tests="\([0-9]*\)".*/\1/' | awk '{sum += $1} END {print sum}')
    FAILURES=$(grep -h "failures=" target/surefire-reports/TEST-*.xml | sed 's/.*failures="\([0-9]*\)".*/\1/' | awk '{sum += $1} END {print sum}')
    ERRORS=$(grep -h "errors=" target/surefire-reports/TEST-*.xml | sed 's/.*errors="\([0-9]*\)".*/\1/' | awk '{sum += $1} END {print sum}')
    
    echo
    print_status "📋 Resumo dos Testes:"
    echo "    Testes executados: $TESTS_RUN"
    echo "    Falhas: $FAILURES"
    echo "    Erros: $ERRORS"
fi

echo
print_status "🎉 Execução de testes concluída!"
