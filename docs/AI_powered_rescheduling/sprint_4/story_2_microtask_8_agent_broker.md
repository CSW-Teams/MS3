# Story 2 Microtask 8 — Agent Broker (Gemma + Llama‑70B)

## Overview
The Agent Broker is the single entry point for AI agent calls. It abstracts provider selection (Gemma vs. Llama‑70B), centralizes timeout/retry handling, and hides provider-specific request/response formats. This mirrors DAO-style abstraction: callers do not know which AI provider is active.

## Architecture & Flow
**Current implementation**
- `AgentBroker` exposes a single method that accepts a TOON payload (plus optional instructions) and returns a **domain response** (`AiScheduleResponse`).
- `AgentBrokerImpl` selects the provider based on configuration and handles retries/timeouts.
- Provider adapters (`GemmaAgentAdapter`, `Llama70bAgentAdapter`) convert the broker request into provider-specific HTTP calls and extract JSON content from the provider response.
- `AiScheduleJsonParser` parses the extracted JSON into DTOs.
- `AiScheduleResponseMapper` converts DTOs into domain objects.

**Sequence (simplified)**
1. Caller builds `AiBrokerRequest` with `.toon` payload.
2. `AgentBrokerImpl` picks provider from `ai.broker.provider`.
3. Provider adapter sends HTTP request.
4. Adapter extracts JSON content from provider response wrapper.
5. JSON is parsed into DTOs, then mapped to domain objects.
6. Broker returns `AiScheduleResponse`.

## Broker API
**Current implementation**
- `AgentBroker#requestSchedule(AiBrokerRequest request)` returns domain object `AiScheduleResponse`.

**Note:** The broker treats `PARTIAL_SUCCESS` and `FAILURE` as errors and raises `AiProtocolException`.

## Provider Adapters
### Gemma (Gemini API)
**Current implementation**
- Uses `ai.broker.gemma-url` and `ai.broker.gemma-api-key`.
- Sends prompt + TOON content in a single user message.
- Expects JSON content inside `candidates[0].content.parts[0].text`.

### Llama‑70B (Groq/OpenAI‑compatible)
**Current implementation**
- Uses `ai.broker.llama70b-url`, `ai.broker.llama70b-api-key`, and `ai.broker.llama70b-model`.
- Sends prompt + TOON content in a single user message.
- Requests `response_format = {"type": "json_object"}`.
- Expects JSON content inside `choices[0].message.content`.

## Configuration
**Current implementation** (application properties)
```
ai.broker.provider=GEMMA
ai.broker.gemma-url=
ai.broker.gemma-api-key=
ai.broker.llama70b-url=
ai.broker.llama70b-api-key=
ai.broker.llama70b-model=llama3-70b-8192
ai.broker.connect-timeout=5s
ai.broker.read-timeout=60s
ai.broker.total-timeout=90s
ai.broker.max-retries=3
ai.broker.retry-backoff=0ms
```

## Timeout & Retry Policy
**Current implementation**
- `connectTimeout`, `readTimeout`, `totalTimeout` are configurable.
- Retry count and backoff are configurable.
- Total timeout is enforced at broker level across all attempts.

## Protocol Handling
**Current implementation**
- Broker expects `.toon` input and enforces JSON-only output.
- JSON parsing uses `AiScheduleJsonParser`.
- Non-success AI statuses are treated as business-domain errors.

## Testing
**Current implementation**
- Unit tests cover:
  - Provider routing based on configuration.
  - Retry behavior until success.
  - Partial success handling as error.

## GDPR & Data Minimization
**Current implementation**
- No personal data is logged within the broker or adapters.
- Payload content is limited to provided TOON input.

## Future Sections (to be completed)
### End-to-end orchestration usage
This section will be defined in the future once orchestration services are integrated with the broker.

### Error taxonomy mapping to API responses
This section will be defined in the future once API endpoints and error translation layers are finalized.

### Observability & audit logging
This section will be defined in the future after logging requirements are approved.
