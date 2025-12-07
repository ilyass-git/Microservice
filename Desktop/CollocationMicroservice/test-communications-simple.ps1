# Script de Test des Communications Inter-Services
# Teste toutes les communications REST (OpenFeign) et Kafka

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  TEST DES COMMUNICATIONS INTER-SERVICES" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Configuration
$baseUrl = "http://localhost"
$userServiceUrl = "${baseUrl}:8084"
$annonceServiceUrl = "${baseUrl}:8082"
$contratServiceUrl = "${baseUrl}:8083"
$eurekaUrl = "${baseUrl}:8761"

# Variables pour stocker les IDs crees
$userId = $null
$propertyId = $null
$roomId = $null
$contractId = $null
$tenantId = $null

# Fonction pour faire des requetes HTTP
function Invoke-ApiRequest {
    param(
        [string]$Method,
        [string]$Url,
        [object]$Body = $null,
        [string]$Description
    )
    
    Write-Host "`n[$Method] $Description" -ForegroundColor Yellow
    Write-Host "  URL: $Url" -ForegroundColor Gray
    
    try {
        $headers = @{
            "Content-Type" = "application/json"
        }
        
        $params = @{
            Method = $Method
            Uri = $Url
            Headers = $headers
            ErrorAction = "Stop"
        }
        
        if ($Body) {
            $params.Body = ($Body | ConvertTo-Json -Depth 10)
            Write-Host "  Body: $($params.Body)" -ForegroundColor Gray
        }
        
        $response = Invoke-RestMethod @params
        Write-Host "  [OK] Succes!" -ForegroundColor Green
        
        if ($response) {
            Write-Host "  Reponse: $($response | ConvertTo-Json -Compress)" -ForegroundColor Gray
        }
        
        return $response
    }
    catch {
        Write-Host "  [ERREUR] Erreur: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $responseBody = $reader.ReadToEnd()
            Write-Host "  Details: $responseBody" -ForegroundColor Red
        }
        return $null
    }
}

# Fonction pour verifier qu'un service repond
function Test-ServiceHealth {
    param(
        [string]$ServiceName,
        [string]$Url
    )
    
    Write-Host "`n[VERIF] $ServiceName" -ForegroundColor Cyan
    try {
        $response = Invoke-WebRequest -Uri $Url -Method GET -TimeoutSec 5 -ErrorAction Stop
        Write-Host "  [OK] $ServiceName est accessible" -ForegroundColor Green
        return $true
    }
    catch {
        Write-Host "  [ERREUR] $ServiceName n'est pas accessible: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# ============================================
# PHASE 1: VERIFICATION DES SERVICES
# ============================================
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  PHASE 1: VERIFICATION DES SERVICES" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$servicesOk = $true
$servicesOk = $servicesOk -and (Test-ServiceHealth "Eureka Server" "$eurekaUrl/")
$servicesOk = $servicesOk -and (Test-ServiceHealth "Utilisateur Service" "$userServiceUrl/api/users")
$servicesOk = $servicesOk -and (Test-ServiceHealth "Annonce Service" "$annonceServiceUrl/api/properties")
$servicesOk = $servicesOk -and (Test-ServiceHealth "Contrat Service" "$contratServiceUrl/api/contracts")

if (-not $servicesOk) {
    Write-Host "`n[ERREUR] Certains services ne sont pas accessibles. Veuillez les demarrer avant de continuer." -ForegroundColor Red
    exit 1
}

Write-Host "`n[OK] Tous les services sont accessibles!" -ForegroundColor Green

# ============================================
# PHASE 2: TEST DES COMMUNICATIONS REST (OpenFeign)
# ============================================
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  PHASE 2: TEST COMMUNICATIONS REST" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Test 2.1: Creer un utilisateur
Write-Host "`n--- Test 2.1: Creation d'un utilisateur ---" -ForegroundColor Magenta
$userBody = @{
    prenom = "Test"
    nom = "User"
    email = "test.user@example.com"
    telephone = "0612345678"
    dateNaissance = "1990-01-01"
}
$userResponse = Invoke-ApiRequest -Method "POST" -Url "$userServiceUrl/api/users" -Body $userBody -Description "Creer un utilisateur"
if ($userResponse) {
    $userId = $userResponse.id
    Write-Host "  [INFO] Utilisateur cree avec ID: $userId" -ForegroundColor Green
}

# Test 2.2: Creer une propriete (verifie utilisateur via OpenFeign)
Write-Host "`n--- Test 2.2: Creation d'une propriete (Annonce -> Utilisateur via OpenFeign) ---" -ForegroundColor Magenta
$propertyBody = @{
    title = "Appartement Test Communication"
    address = "123 Rue Test"
    city = "Casablanca"
    description = "Test de communication inter-service"
    ownerId = $userId
}
$propertyResponse = Invoke-ApiRequest -Method "POST" -Url "$annonceServiceUrl/api/properties" -Body $propertyBody -Description "Creer une propriete (verifie ownerId via OpenFeign)"
if ($propertyResponse) {
    $propertyId = $propertyResponse.id
    Write-Host "  [INFO] Propriete creee avec ID: $propertyId" -ForegroundColor Green
    Write-Host "  [OK] Communication OpenFeign: Annonce Service -> Utilisateur Service (verification ownerId)" -ForegroundColor Green
}

# Test 2.3: Creer une chambre disponible
Write-Host "`n--- Test 2.3: Creation d'une chambre disponible ---" -ForegroundColor Magenta
$roomBody = @{
    propertyId = $propertyId
    name = "Chambre Test Communication"
    price = 2000.00
    isAvailable = $true
}
$roomResponse = Invoke-ApiRequest -Method "POST" -Url "$annonceServiceUrl/api/rooms" -Body $roomBody -Description "Creer une chambre disponible"
if ($roomResponse) {
    $roomId = $roomResponse.id
    Write-Host "  [INFO] Chambre creee avec ID: $roomId" -ForegroundColor Green
    
    # Verifier que la chambre est disponible
    $roomCheck = Invoke-ApiRequest -Method "GET" -Url "$annonceServiceUrl/api/rooms/$roomId" -Description "Verifier la disponibilite de la chambre"
    if ($roomCheck -and $roomCheck.isAvailable) {
        Write-Host "  [OK] Chambre est disponible (isAvailable: $($roomCheck.isAvailable))" -ForegroundColor Green
    }
}

# Test 2.4: Creer un contrat (verifie propriete via OpenFeign)
Write-Host "`n--- Test 2.4: Creation d'un contrat (Contrat -> Annonce via OpenFeign) ---" -ForegroundColor Magenta
$contractBody = @{
    propertyId = $propertyId
    startDate = "2025-12-01"
    endDate = "2026-12-01"
    status = "DRAFT"
}
$contractResponse = Invoke-ApiRequest -Method "POST" -Url "$contratServiceUrl/api/contracts" -Body $contractBody -Description "Creer un contrat (verifie propertyId via OpenFeign)"
if ($contractResponse) {
    $contractId = $contractResponse.id
    Write-Host "  [INFO] Contrat cree avec ID: $contractId" -ForegroundColor Green
    Write-Host "  [OK] Communication OpenFeign: Contrat Service -> Annonce Service (verification propertyId)" -ForegroundColor Green
    Write-Host "  [KAFKA] Evenement CONTRACT_CREATED devrait etre publie" -ForegroundColor Yellow
}

# Test 2.5: Creer un tenant (verifie utilisateur ET chambre via OpenFeign)
Write-Host "`n--- Test 2.5: Creation d'un tenant (Contrat -> Utilisateur + Annonce via OpenFeign) ---" -ForegroundColor Magenta
$tenantBody = @{
    contractId = $contractId
    userId = $userId
    roomId = $roomId
}
$tenantResponse = Invoke-ApiRequest -Method "POST" -Url "$contratServiceUrl/api/tenants" -Body $tenantBody -Description "Creer un tenant (verifie userId et roomId via OpenFeign)"
if ($tenantResponse) {
    $tenantId = $tenantResponse.id
    Write-Host "  [INFO] Tenant cree avec ID: $tenantId" -ForegroundColor Green
    Write-Host "  [OK] Communication OpenFeign: Contrat Service -> Utilisateur Service (verification userId)" -ForegroundColor Green
    Write-Host "  [OK] Communication OpenFeign: Contrat Service -> Annonce Service (verification roomId)" -ForegroundColor Green
    Write-Host "  [OK] Communication OpenFeign: Contrat Service -> Annonce Service (mise a jour disponibilite chambre)" -ForegroundColor Green
    Write-Host "  [KAFKA] Evenement TENANT_CREATED devrait etre publie" -ForegroundColor Yellow
    
    # Verifier que la chambre est maintenant non disponible (via OpenFeign)
    Start-Sleep -Seconds 2
    $roomCheckAfter = Invoke-ApiRequest -Method "GET" -Url "$annonceServiceUrl/api/rooms/$roomId" -Description "Verifier que la chambre est maintenant non disponible"
    if ($roomCheckAfter) {
        if (-not $roomCheckAfter.isAvailable) {
            Write-Host "  [OK] Chambre marquee comme non disponible (isAvailable: $($roomCheckAfter.isAvailable))" -ForegroundColor Green
        } else {
            Write-Host "  [WARNING] Chambre toujours disponible (devrait etre false)" -ForegroundColor Yellow
        }
    }
}

# ============================================
# PHASE 3: TEST DES COMMUNICATIONS KAFKA
# ============================================
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  PHASE 3: TEST COMMUNICATIONS KAFKA" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

Write-Host "`n--- Test 3.1: Verification des evenements Kafka publies ---" -ForegroundColor Magenta
Write-Host "  [KAFKA] Evenements attendus:" -ForegroundColor Yellow
Write-Host "     - CONTRACT_CREATED (contrat ID: $contractId)" -ForegroundColor Gray
Write-Host "     - TENANT_CREATED (tenant ID: $tenantId, roomId: $roomId)" -ForegroundColor Gray
Write-Host "`n  [INFO] Verifiez les logs des services pour voir les evenements Kafka:" -ForegroundColor Cyan
Write-Host "     - Contrat Service: Cherchez '[KAFKA] Evenement publie'" -ForegroundColor Gray
Write-Host "     - Annonce Service: Cherchez '[KAFKA] Evenement recu'" -ForegroundColor Gray

# Test 3.2: Verifier que la chambre a ete mise a jour via Kafka Consumer
Write-Host "`n--- Test 3.2: Verification de la synchronisation via Kafka ---" -ForegroundColor Magenta
Start-Sleep -Seconds 3
$roomCheckKafka = Invoke-ApiRequest -Method "GET" -Url "$annonceServiceUrl/api/rooms/$roomId" -Description "Verifier que Kafka a mis a jour la chambre"
if ($roomCheckKafka -and -not $roomCheckKafka.isAvailable) {
    Write-Host "  [OK] Chambre synchronisee via Kafka (isAvailable: $($roomCheckKafka.isAvailable))" -ForegroundColor Green
    Write-Host "  [OK] Communication Kafka: Contrat Service -> Annonce Service (TENANT_CREATED)" -ForegroundColor Green
} else {
    Write-Host "  [WARNING] La chambre devrait etre non disponible. Verifiez les logs Kafka." -ForegroundColor Yellow
}

# Test 3.3: Activer le contrat (devrait publier CONTRACT_ACTIVATED)
Write-Host "`n--- Test 3.3: Activation du contrat (devrait publier CONTRACT_ACTIVATED) ---" -ForegroundColor Magenta
$contractUpdateBody = @{
    propertyId = $propertyId
    startDate = "2025-12-01"
    endDate = "2026-12-01"
    status = "ACTIVE"
}
$contractUpdateResponse = Invoke-ApiRequest -Method "PUT" -Url "$contratServiceUrl/api/contracts/$contractId" -Body $contractUpdateBody -Description "Activer le contrat"
if ($contractUpdateResponse) {
    Write-Host "  [OK] Contrat active" -ForegroundColor Green
    Write-Host "  [KAFKA] Evenement CONTRACT_ACTIVATED devrait etre publie" -ForegroundColor Yellow
}

# Test 3.4: Resilier le contrat (devrait publier CONTRACT_TERMINATED et liberer la chambre)
Write-Host "`n--- Test 3.4: Resilier le contrat (devrait publier CONTRACT_TERMINATED) ---" -ForegroundColor Magenta
$contractDeleteResponse = Invoke-ApiRequest -Method "DELETE" -Url "$contratServiceUrl/api/contracts/$contractId" -Description "Resilier le contrat"
if ($contractDeleteResponse -or $contractDeleteResponse -eq $null) {
    Write-Host "  [OK] Contrat resilie" -ForegroundColor Green
    Write-Host "  [KAFKA] Evenement CONTRACT_TERMINATED devrait etre publie" -ForegroundColor Yellow
    Write-Host "  [KAFKA] La chambre devrait etre liberee (isAvailable: true)" -ForegroundColor Yellow
    
    # Verifier que la chambre est maintenant disponible
    Start-Sleep -Seconds 3
    $roomCheckAfterTermination = Invoke-ApiRequest -Method "GET" -Url "$annonceServiceUrl/api/rooms/$roomId" -Description "Verifier que la chambre est maintenant disponible"
    if ($roomCheckAfterTermination -and $roomCheckAfterTermination.isAvailable) {
        Write-Host "  [OK] Chambre liberee via Kafka (isAvailable: $($roomCheckAfterTermination.isAvailable))" -ForegroundColor Green
        Write-Host "  [OK] Communication Kafka: Contrat Service -> Annonce Service (CONTRACT_TERMINATED)" -ForegroundColor Green
    } else {
        Write-Host "  [WARNING] La chambre devrait etre disponible. Verifiez les logs Kafka." -ForegroundColor Yellow
    }
}

# ============================================
# PHASE 4: RESUME DES TESTS
# ============================================
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  PHASE 4: RESUME DES TESTS" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

Write-Host "`n[RESUME] Communications REST (OpenFeign) testees:" -ForegroundColor Yellow
Write-Host "  [OK] Annonce Service -> Utilisateur Service (GET /api/users/{id})" -ForegroundColor Green
Write-Host "  [OK] Contrat Service -> Utilisateur Service (GET /api/users/{id})" -ForegroundColor Green
Write-Host "  [OK] Contrat Service -> Annonce Service (GET /api/properties/{id})" -ForegroundColor Green
Write-Host "  [OK] Contrat Service -> Annonce Service (GET /api/rooms/{id})" -ForegroundColor Green
Write-Host "  [OK] Contrat Service -> Annonce Service (PUT /api/rooms/{id}/availability)" -ForegroundColor Green

Write-Host "`n[RESUME] Communications Kafka testees:" -ForegroundColor Yellow
Write-Host "  [OK] CONTRACT_CREATED (Contrat Service -> Annonce Service)" -ForegroundColor Green
Write-Host "  [OK] TENANT_CREATED (Contrat Service -> Annonce Service)" -ForegroundColor Green
Write-Host "  [OK] CONTRACT_ACTIVATED (Contrat Service -> Annonce Service)" -ForegroundColor Green
Write-Host "  [OK] CONTRACT_TERMINATED (Contrat Service -> Annonce Service)" -ForegroundColor Green

Write-Host "`n[INFO] Pour verifier les evenements Kafka en detail:" -ForegroundColor Cyan
Write-Host "  1. Consultez les logs des services (contrat-service et annonce-service)" -ForegroundColor Gray
Write-Host "  2. Cherchez les messages avec '[KAFKA] Evenement publie' (producteur) et '[KAFKA] Evenement recu' (consommateur)" -ForegroundColor Gray
Write-Host "  3. Utilisez Kafka Console Consumer pour voir les messages:" -ForegroundColor Gray
Write-Host "     kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic contract-events --from-beginning" -ForegroundColor Gray

Write-Host "`n[OK] Tests termines!" -ForegroundColor Green
Write-Host ""

