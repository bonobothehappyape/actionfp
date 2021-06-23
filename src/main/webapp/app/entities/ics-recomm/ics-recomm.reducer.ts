import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IICSRecomm, defaultValue } from 'app/shared/model/ics-recomm.model';

export const ACTION_TYPES = {
  SEARCH_ICSRECOMMS: 'iCSRecomm/SEARCH_ICSRECOMMS',
  FETCH_ICSRECOMM_LIST: 'iCSRecomm/FETCH_ICSRECOMM_LIST',
  FETCH_ICSRECOMM: 'iCSRecomm/FETCH_ICSRECOMM',
  CREATE_ICSRECOMM: 'iCSRecomm/CREATE_ICSRECOMM',
  UPDATE_ICSRECOMM: 'iCSRecomm/UPDATE_ICSRECOMM',
  PARTIAL_UPDATE_ICSRECOMM: 'iCSRecomm/PARTIAL_UPDATE_ICSRECOMM',
  DELETE_ICSRECOMM: 'iCSRecomm/DELETE_ICSRECOMM',
  RESET: 'iCSRecomm/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IICSRecomm>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

export type ICSRecommState = Readonly<typeof initialState>;

// Reducer

export default (state: ICSRecommState = initialState, action): ICSRecommState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_ICSRECOMMS):
    case REQUEST(ACTION_TYPES.FETCH_ICSRECOMM_LIST):
    case REQUEST(ACTION_TYPES.FETCH_ICSRECOMM):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_ICSRECOMM):
    case REQUEST(ACTION_TYPES.UPDATE_ICSRECOMM):
    case REQUEST(ACTION_TYPES.DELETE_ICSRECOMM):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_ICSRECOMM):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.SEARCH_ICSRECOMMS):
    case FAILURE(ACTION_TYPES.FETCH_ICSRECOMM_LIST):
    case FAILURE(ACTION_TYPES.FETCH_ICSRECOMM):
    case FAILURE(ACTION_TYPES.CREATE_ICSRECOMM):
    case FAILURE(ACTION_TYPES.UPDATE_ICSRECOMM):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_ICSRECOMM):
    case FAILURE(ACTION_TYPES.DELETE_ICSRECOMM):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.SEARCH_ICSRECOMMS):
    case SUCCESS(ACTION_TYPES.FETCH_ICSRECOMM_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.FETCH_ICSRECOMM):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_ICSRECOMM):
    case SUCCESS(ACTION_TYPES.UPDATE_ICSRECOMM):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_ICSRECOMM):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_ICSRECOMM):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {},
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

const apiUrl = 'api/ics-recomms';
const apiSearchUrl = 'api/_search/ics-recomms';

// Actions

export const getSearchEntities: ICrudSearchAction<IICSRecomm> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_ICSRECOMMS,
  payload: axios.get<IICSRecomm>(`${apiSearchUrl}?query=${query}`),
});

export const getEntities: ICrudGetAllAction<IICSRecomm> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_ICSRECOMM_LIST,
  payload: axios.get<IICSRecomm>(`${apiUrl}?cacheBuster=${new Date().getTime()}`),
});

export const getEntity: ICrudGetAction<IICSRecomm> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_ICSRECOMM,
    payload: axios.get<IICSRecomm>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IICSRecomm> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_ICSRECOMM,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IICSRecomm> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_ICSRECOMM,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IICSRecomm> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_ICSRECOMM,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IICSRecomm> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_ICSRECOMM,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
