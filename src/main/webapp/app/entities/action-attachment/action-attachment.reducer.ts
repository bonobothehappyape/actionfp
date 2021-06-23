import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IActionAttachment, defaultValue } from 'app/shared/model/action-attachment.model';

export const ACTION_TYPES = {
  SEARCH_ACTIONATTACHMENTS: 'actionAttachment/SEARCH_ACTIONATTACHMENTS',
  FETCH_ACTIONATTACHMENT_LIST: 'actionAttachment/FETCH_ACTIONATTACHMENT_LIST',
  FETCH_ACTIONATTACHMENT: 'actionAttachment/FETCH_ACTIONATTACHMENT',
  CREATE_ACTIONATTACHMENT: 'actionAttachment/CREATE_ACTIONATTACHMENT',
  UPDATE_ACTIONATTACHMENT: 'actionAttachment/UPDATE_ACTIONATTACHMENT',
  PARTIAL_UPDATE_ACTIONATTACHMENT: 'actionAttachment/PARTIAL_UPDATE_ACTIONATTACHMENT',
  DELETE_ACTIONATTACHMENT: 'actionAttachment/DELETE_ACTIONATTACHMENT',
  SET_BLOB: 'actionAttachment/SET_BLOB',
  RESET: 'actionAttachment/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IActionAttachment>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

export type ActionAttachmentState = Readonly<typeof initialState>;

// Reducer

export default (state: ActionAttachmentState = initialState, action): ActionAttachmentState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_ACTIONATTACHMENTS):
    case REQUEST(ACTION_TYPES.FETCH_ACTIONATTACHMENT_LIST):
    case REQUEST(ACTION_TYPES.FETCH_ACTIONATTACHMENT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_ACTIONATTACHMENT):
    case REQUEST(ACTION_TYPES.UPDATE_ACTIONATTACHMENT):
    case REQUEST(ACTION_TYPES.DELETE_ACTIONATTACHMENT):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_ACTIONATTACHMENT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.SEARCH_ACTIONATTACHMENTS):
    case FAILURE(ACTION_TYPES.FETCH_ACTIONATTACHMENT_LIST):
    case FAILURE(ACTION_TYPES.FETCH_ACTIONATTACHMENT):
    case FAILURE(ACTION_TYPES.CREATE_ACTIONATTACHMENT):
    case FAILURE(ACTION_TYPES.UPDATE_ACTIONATTACHMENT):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_ACTIONATTACHMENT):
    case FAILURE(ACTION_TYPES.DELETE_ACTIONATTACHMENT):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.SEARCH_ACTIONATTACHMENTS):
    case SUCCESS(ACTION_TYPES.FETCH_ACTIONATTACHMENT_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.FETCH_ACTIONATTACHMENT):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_ACTIONATTACHMENT):
    case SUCCESS(ACTION_TYPES.UPDATE_ACTIONATTACHMENT):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_ACTIONATTACHMENT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_ACTIONATTACHMENT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {},
      };
    case ACTION_TYPES.SET_BLOB: {
      const { name, data, contentType } = action.payload;
      return {
        ...state,
        entity: {
          ...state.entity,
          [name]: data,
          [name + 'ContentType']: contentType,
        },
      };
    }
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

const apiUrl = 'api/action-attachments';
const apiSearchUrl = 'api/_search/action-attachments';

// Actions

export const getSearchEntities: ICrudSearchAction<IActionAttachment> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_ACTIONATTACHMENTS,
  payload: axios.get<IActionAttachment>(`${apiSearchUrl}?query=${query}`),
});

export const getEntities: ICrudGetAllAction<IActionAttachment> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_ACTIONATTACHMENT_LIST,
  payload: axios.get<IActionAttachment>(`${apiUrl}?cacheBuster=${new Date().getTime()}`),
});

export const getEntity: ICrudGetAction<IActionAttachment> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_ACTIONATTACHMENT,
    payload: axios.get<IActionAttachment>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IActionAttachment> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_ACTIONATTACHMENT,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IActionAttachment> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_ACTIONATTACHMENT,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IActionAttachment> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_ACTIONATTACHMENT,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IActionAttachment> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_ACTIONATTACHMENT,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const setBlob = (name, data, contentType?) => ({
  type: ACTION_TYPES.SET_BLOB,
  payload: {
    name,
    data,
    contentType,
  },
});

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
